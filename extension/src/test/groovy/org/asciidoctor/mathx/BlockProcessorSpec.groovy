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