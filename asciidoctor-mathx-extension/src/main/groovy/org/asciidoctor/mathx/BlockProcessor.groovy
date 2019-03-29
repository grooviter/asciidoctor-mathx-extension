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
import org.asciidoctor.ast.AbstractBlock
import org.asciidoctor.ast.DocumentRuby
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

    /**
     * Establishes the pattern under which this processor is going to be called passing the required
     * values to the super constructor. Every time there's a block named 'mathx' and of type
     * `listing` + `paragraph` this processor will be hit.
     *
     * @since 0.1.0
     */
    BlockProcessor() {
        super('mathx', [contexts: [':listing', ':paragraph']] as Map<String, Object>)
    }

    @Override
    Object process(AbstractBlock parent, Reader reader, Map<String, Object> blockAttributes) {
        DocumentRuby document = parent.document
        Map<String, Object> documentAttributes = document.attributes

        String formula = reader.readLines().join('\n')
        String hash = Utils.getMD5(formula)
        File realImageFile = getRealImageFile(hash, documentAttributes)

        if (!realImageFile.exists()) {
            BufferedImage image = Utils.TryOrLogError("Error while generating mathx image") {
                Utils.createImageFromLatex(formula, blockAttributes)
            }

            ImageIO.write(image, "png", realImageFile)
        }

        Map<String,Object> imageAttrs = [
            target: realImageFile.name,
            alt: realImageFile.name,
            title: blockAttributes.title,
            width: blockAttributes.width,
            height: blockAttributes.height
        ]

        return createBlock(parent, "image", "", imageAttrs, Constants.EMPTY_MAP_OBJ_OBJ)
    }

    /**
     * Resolves where to write the latex image
     *
     * @param formulaHash hash which be used as the file name
     * @param documentAttributes {@link DocumentRuby} attributes*
     * @return the real path of the image file
     */
    private File getRealImageFile(String formulaHash, Map<String, Object> documentAttributes) {
        String documentDir = documentAttributes.docdir?.toString()
        String imagesOutputDir = documentAttributes.imagesoutdir?.toString()
        String imagesDir = documentAttributes.imagesdir?.toString()

        File images = imagesOutputDir
          ? getOutputImagesDir(documentDir, imagesOutputDir)
          : getImagesDir(imagesDir)

        String filename= "${formulaHash}.png"
        File realImagePath = new File(images, filename)

        return realImagePath
    }

    /**
     * Resolves the real images dir from the 'images' entry in document
     * configuration
     *
     * @param imagesPath image path found in document configuration
     * @return where to find the real
     */
    private File getImagesDir(String imagesPath) {
        File imagesDir = new File(imagesPath)

        if (imagesDir.isAbsolute()) {
            return imagesDir
        }

        return new File(this.rubyRuntime.currentDirectory, imagesPath)
    }

    /**
     * Resolves the real images dir from the 'imagesoutput' entry in document
     * configuration
     *
     * Resolves where is the real image file given an output dir. If the
     * output directory is absolute, then it's used, otherwise we resolve
     * where the document is and then we concatenate the output dir to it
     *
     * @param documentDir where is the document
     * @param imagesOutputPath the declared output dir in configuration
     * @return the output dir where to find the image
     */
    private static File getOutputImagesDir(String documentDir, String imagesOutputPath) {
        File imagesOutputDir = new File(imagesOutputPath)

        if (imagesOutputDir.isAbsolute()) {
            return imagesOutputDir
        }

        return new File(documentDir, imagesOutputPath)
    }
}
