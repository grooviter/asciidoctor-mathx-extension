/*
 * Copyright 2018-2019 Mario Garcia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.asciidoctor.mathx

import groovy.transform.CompileStatic
import org.asciidoctor.ast.StructuralNode
import org.asciidoctor.extension.BlockProcessor as AsciidoctorBlockProcessor
import org.asciidoctor.extension.Reader

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
/**
 * This processor is going to transform a listing paragraph block into
 * an image with a mathematical expression
 *
 * @since 0.1.0
 */
@CompileStatic
class BlockProcessor extends AsciidoctorBlockProcessor {

    static final String TEMPLATE_TITLE = 'title="$it"'
    static final String TEMPLATE_ALT = 'alt="$it"'
    static final String TEMPLATE_ALIGN ='align="$it"'
    static final String TEMPLATE_WIDTH ='width=$it'
    static final String TEMPLATE_HEIGHT = 'height=$it'

    static final Closure<Object> ONLY_WITH_VALUE = { Map.Entry<String, Object> entry -> entry.value }

    static final String DOCUMENT_ATTR_IMAGES_DIR = 'imagesdir'
    static final String DOCUMENT_ATTR_GRADLE_PROJECT_DIR = 'gradle-projectdir'

    static final String PROCESSOR_NAME = 'mathx'
    static final Map<String,Object> PROCESSOR_CONFIG = [contexts: [':listing', ':paragraph']] as Map<String, Object>

    /**
      * Establishes the pattern under which this processor is going to be called passing the required
      * values to the super constructor. Every time there's a block named 'mathx' and of type
      * `listing` + `paragraph` this processor will be hit.
      *
      * @since 0.1.0
      */
    BlockProcessor() {
        super(PROCESSOR_NAME, PROCESSOR_CONFIG)
    }

    @Override
    Object process(StructuralNode node, Reader reader, Map<String, Object> blockAttrs) {
        Integer width = blockAttrs.width as Integer
        Integer height = blockAttrs.height as Integer
        String align = blockAttrs.align
        String title = blockAttrs.title
        String name = blockAttrs.name

        String formula = reader.readLines().join('\n')
        File file = createImagePath(node, formula, name)
        BufferedImage image = createImage(file, formula, width, height)

        if (!image) {
            return null
        }

        Map<String,Object> imageAttrs = [
            target: file.name,
            alt: file.name,
            title: title,
            align: align,
            width: width,
            height: height
        ] as Map<String, Object>

        List<String> lines = createImageLines(imageAttrs.findAll(ONLY_WITH_VALUE))
        parseContent(node, lines)

        return null
    }

    private static BufferedImage createImage(File imageFile, String formula, Integer width, Integer height) {
        return Utils.TryOrLogError("Error while generating mathematical expression image") {
            BufferedImage image = Utils.createImageFromLatex(formula)
            BufferedImage resized = Utils.resizeImage(image, width, height)

            ImageIO.write(resized, "png", imageFile)

            return resized
        }
    }

    private static List<String> createImageLines(Map<String, Object> attrs) {
        String target = attrs.target
        String caption = attrs.title ? ".${attrs.title}" : null

        String title = Utils.renderIfPresent(attrs.title, TEMPLATE_TITLE)
        String alt =  Utils.renderIfPresent(attrs.alt, TEMPLATE_ALT)
        String align = Utils.renderIfPresent(attrs.align, TEMPLATE_ALIGN)
        String width = Utils.renderIfPresent(attrs.width, TEMPLATE_WIDTH)
        String height = Utils.renderIfPresent(attrs.height, TEMPLATE_HEIGHT)

        String properties = [alt, width, height, title, align]
                .grep()
                .join(',')
                .toString()

        // In order to get an auto-numbered title with caption (e.g., Figure N. Title), and make the alignment to work
        // you need to use the block image macro.
        // The block image macro (like all block macros) uses two colons instead of one.
        return [caption, "image::$target[$properties]"].grep()*.toString()
    }

    private static File createImagePath(StructuralNode node, String formula, String customName) {
        String filename = "${Utils.getMD5(formula)}.png"
        String finalName = customName ?: filename

        Map<String,Object> attrs = node.document.attributes
        String projectDir = attrs[DOCUMENT_ATTR_GRADLE_PROJECT_DIR]
        String imagesDir = attrs[DOCUMENT_ATTR_IMAGES_DIR]

        String filePath = new File("$projectDir/$imagesDir", finalName).absolutePath
        String file = node.normalizeWebPath(filePath, "", true)
        File fileToCreate = new File(file)

        fileToCreate.parentFile.mkdirs()

        return fileToCreate
    }
}
