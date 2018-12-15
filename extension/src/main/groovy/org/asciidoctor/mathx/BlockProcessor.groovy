package org.asciidoctor.mathx

import groovy.text.Template
import groovy.transform.CompileStatic
import org.asciidoctor.ast.AbstractBlock
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

    private static final Map<String, Object> EMPTY_MAP_ST_OBJ = [:] as Map<String, Object>
    private static final Map<Object, Object> EMPTY_MAP_OBJ_OBJ = [:] as Map<Object, Object>

    private static final Template TEMPLATE = Utils.createTemplate('template.tpl')
    private static final Template TEMPLATE_NO_TITLE = Utils.createTemplate('templateNoTitle.tpl')

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
    Object process(AbstractBlock parent, Reader reader, Map<String, Object> attributes) {
        String formula = reader.readLines().join('\n')
        String fileName = Utils.getMD5(formula)
        File imagePath = new File(imageDirectory, "${fileName}.png")
        String content = renderBlock(imagePath, attributes)

        if (!imagePath.exists()) {
            BufferedImage image = Utils.createImageFromLatex(formula)
            ImageIO.write(image, "png", imagePath)
        }

        return createBlock(parent, "pass", content, EMPTY_MAP_ST_OBJ, EMPTY_MAP_OBJ_OBJ)
    }

    private File getImageDirectory() {
        String currentDirectory = this.rubyRuntime.currentDirectory
        File outputImageDir = new File(currentDirectory, 'images')

        return outputImageDir
    }

    private static String renderBlock(File imagePath, Map<String, Object> attributes) {
        Map<String, Object> bindings = [
            imagePath: imagePath,
            title: attributes.title,
            alt: attributes.alt,
            width: attributes.width,
            height: attributes.height
        ]

        StringWriter stringWriter = new StringWriter()

        Template template = attributes?.title ? TEMPLATE : TEMPLATE_NO_TITLE
        template.make(bindings).writeTo(stringWriter)

        return stringWriter.toString()
    }
}
