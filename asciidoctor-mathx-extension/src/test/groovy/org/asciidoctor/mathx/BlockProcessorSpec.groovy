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

import org.asciidoctor.Asciidoctor
import org.asciidoctor.OptionsBuilder
import spock.lang.Specification

class BlockProcessorSpec extends Specification {

    void setup() {
        new File('build').mkdirs()
    }

    void 'check pdf'() {
        given: 'an asciidoctor document'
        def document = '''
            = My document
            Hello World
            
            [mathx, width=175, height=120]
            ----
            x^2 + bx + c
            ----    
        '''
        and: 'expected output file paths'
        def build = new File('build')
        def file = new File('mathx.html')

        when: 'converting it to pdf'
        Asciidoctor asciidoctor = Asciidoctor.Factory.create()
        asciidoctor.javaExtensionRegistry().block(new BlockProcessor())

        asciidoctor.convert(document, OptionsBuilder
            .options()
            .toDir(build)
            .toFile(file)
            .backend('html'))

        then: 'we should get the content'
        new File(build, file.name).exists()
    }
}