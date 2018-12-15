[![license](https://img.shields.io/github/license/grooviter/asciidoctor-formulax-extension.svg)](https://www.apache.org/licenses/LICENSE-2.0) [![Travis](https://img.shields.io/travis/grooviter/asciidoctor-formulax-extension.svg)](https://travis-ci.org/grooviter/asciidoctor-formulax-extension) [![Bintray](https://img.shields.io/bintray/v/grooviter/maven/asciidoctor-formulax-extension.svg)](https://bintray.com/grooviter/maven/asciidoctor-formulax-extension)

# asciidoctor-mathx-extension

## Intro

This asciidoctor extension uses LateX mathematical syntax to create
images showing mathematical expressions. All you have to do is to
introduce a LateX mathematical formula within a `formula` block and it
will be converted into an image, e.g:

```
[mathx, width=200, height=100]
.2nd grade equations
----
x^2 + x + \text{1}\ = \text{0}\
----
```

## Gradle dependency

In order to use the extension in your code you can find it in Bintray
or Maven Central:

```groovy
repositories {
    jcenter()
}
```

Then add the dependency:

```groovy
compile 'com.github.grooviter:asciidoctor-mathx-extension:0.1.0'
```
