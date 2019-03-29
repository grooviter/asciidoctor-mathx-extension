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
import groovy.util.logging.Log
import org.scilab.forge.jlatexmath.TeXConstants
import org.scilab.forge.jlatexmath.TeXFormula
import org.scilab.forge.jlatexmath.TeXIcon

import javax.swing.JLabel
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.security.MessageDigest
import java.util.logging.Level

/**
 * Utility classes
 *
 * @since 0.1.0
 */
@Log
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
     * @param blockAttributes
     * @return the image with the mathematical expression
     * @since 0.1.0
     */
    static BufferedImage createImageFromLatex(String latexFormula, Map<String, Object> blockAttributes) {
        TeXFormula formula = new TeXFormula(latexFormula)
        TeXIcon icon = TeXFormula.TeXIconBuilder.newInstance(formula)
            .setStyle(TeXConstants.STYLE_DISPLAY)
            .setSize(blockAttributes?.width?.toString()?.toInteger() ?: Constants.DEFAULT_WIDTH)
            .build()

        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_BYTE_GRAY)
        Graphics2D g2 = image.createGraphics()
        g2.setColor(Color.white)
        g2.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight())
        JLabel jl = new JLabel()
        jl.setForeground(new Color(0, 0, 0))
        icon.paintIcon(jl, g2, 0, 0)

        return image
    }

    /**
     * Utility function to wrap a possible failing function to
     * capture the possible error
     *
     * @param errorMessage init of the possible error message
     * @param action possible failing function
     * @return a value
     * @since 0.1.2
     */
    static <T> T TryOrLogError(String errorMessage, Closure<T> action) {
        T result = null

        try {
            result = action()
        } catch(Throwable th) {
            log.log(Level.SEVERE, "${errorMessage}: ${th.message}")
        }

        return result
    }
}
