package org.asciidoctor.mathx

import groovy.transform.CompileStatic
import org.asciidoctor.Asciidoctor
import org.asciidoctor.extension.spi.ExtensionRegistry

/**
 * Declares the MathX asciidoctor extension
 *
 * @since 0.1.0
 */
@CompileStatic
class Extension implements ExtensionRegistry {

    @Override
    void register(Asciidoctor ascii) {
        ascii
            .javaExtensionRegistry()
            .block(new BlockProcessor())
    }
}
