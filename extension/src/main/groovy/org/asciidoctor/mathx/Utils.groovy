package org.asciidoctor.mathx

import groovy.text.Template
import groovy.text.markup.MarkupTemplateEngine
import groovy.transform.CompileStatic
import org.scilab.forge.jlatexmath.TeXConstants
import org.scilab.forge.jlatexmath.TeXFormula
import org.scilab.forge.jlatexmath.TeXIcon

import javax.swing.JLabel
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.security.MessageDigest

/**
 * Utility classes
 *
 * @since 0.1.0
 */
@CompileStatic
class Utils {

    /**
     * Generates an MD5 hash of the {@link String} passed as argument
     *
     * @param content the string we want its MD5 from
     * @return an MD5 hash
     * @since 0.1.0
     */
    static String getMD5(String content) {
        return MessageDigest
            .getInstance("MD5")
            .digest(content.bytes)
            .encodeHex()
            .toString()
    }

    /**
     * Generates a {@link BufferedImage} from a given Latex math expression passed
     * as parameter
     *
     * @param latexFormula the formula we want an image from
     * @return the image with the mathematical expression
     * @since 0.1.0
     */
    static BufferedImage createImageFromLatex(String latexFormula) {
        TeXFormula formula = new TeXFormula(latexFormula)
        TeXIcon icon = TeXFormula.TeXIconBuilder.newInstance(formula)
                .setStyle(TeXConstants.STYLE_DISPLAY)
                .setSize(60)
                .setWidth(TeXConstants.UNIT_CM, 4, TeXConstants.ALIGN_LEFT)
                .setInterLineSpacing(TeXConstants.UNIT_CM, 0.5f)
                .build()

        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_BYTE_GRAY)
        Graphics2D g2 = image.createGraphics()
        g2.setColor(Color.white)
        g2.fillRect(0,0, icon.getIconWidth(),icon.getIconHeight())
        JLabel jl = new JLabel()
        jl.setForeground(new Color(0, 0, 0))
        icon.paintIcon(jl, g2, 0, 0)

        return image
    }

    /**
     * Generates an instance of the template representing the image containing the mathematical
     * expression. This template contains the image's title.
     *
     * @param templateName name of the template you want to create
     * @return a {@link Template} containing the image with the mathematical expression
     * @since 0.1.0
     */
    static Template createTemplate(String templateName) {
        MarkupTemplateEngine engine = new MarkupTemplateEngine()
        URL template = Utils.getResource(templateName)

        return engine.createTemplate(template)
    }
}
