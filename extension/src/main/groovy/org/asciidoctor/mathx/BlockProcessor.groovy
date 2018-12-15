package org.asciidoctor.mathx

import groovy.text.Template
import groovy.transform.CompileStatic
import org.asciidoctor.ast.AbstractBlock
import org.asciidoctor.ast.DocumentRuby
import org.asciidoctor.extension.BlockProcessor as AsciidoctorBlockProcessor
import org.asciidoctor.extension.Reader

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.file.Paths

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
    Object process(AbstractBlock parent, Reader reader, Map<String, Object> blockAttributes) {
        DocumentRuby document = parent.document
        Map<String, Object> documentAttributes = document.attributes

        String formula = reader.readLines().join('\n')
        String hash = Utils.getMD5(formula)
        File realImageFile = getRealImageFile(hash, documentAttributes)
        String relativeImagePath = getRelativeImagePath(hash, documentAttributes)

        if (!realImageFile.exists()) {
            BufferedImage image = Utils.createImageFromLatex(formula)
            ImageIO.write(image, "png", realImageFile)
        }

        String content = renderBlock(relativeImagePath, blockAttributes)
        return createBlock(parent, "pass", content, EMPTY_MAP_ST_OBJ, EMPTY_MAP_OBJ_OBJ)
    }

    /**
     * The result of this method will be used in the image block as the 'src' value. Normally it should be
     * a relative path
     *
     * @param formulaHash hash which be used as the file name
     * @param documentAttributes {@link DocumentRuby} attributes
     * @return the image relative path
     */
    private static String getRelativeImagePath(String formulaHash, Map<String, Object> documentAttributes) {
        String filename= "${formulaHash}.png"
        String relativePath = Paths
                .get("${documentAttributes.imagesdir}", filename)
                .toString()

        return relativePath
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

    /**
     * Renders the html of the image block
     *
     * @param imagePath path to be used as the src value in the rendered html
     * @param blockAttributes attributes found in the mathx block
     * @return the
     */
    private static String renderBlock(String imagePath, Map<String, Object> blockAttributes) {
        Map<String, Object> bindings = [
            imagePath: imagePath,
            title: blockAttributes.title,
            alt: blockAttributes.alt,
            width: blockAttributes.width,
            height: blockAttributes.height
        ]

        StringWriter stringWriter = new StringWriter()

        Template template = blockAttributes?.title ? TEMPLATE : TEMPLATE_NO_TITLE
        template.make(bindings).writeTo(stringWriter)

        return stringWriter.toString()
    }
}
