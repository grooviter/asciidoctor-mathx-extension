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
