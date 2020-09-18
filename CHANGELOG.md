# Change log
All notable changes to this project will be documented in this file.

## [0.1.5] - [2020-09-19]

### Fixed

- Use 'imagesoutdir' property value if present

## [0.1.4] - [2020-09-18]

### Added

- Kordamp Gradle plugins
- License headers to Groovy source code

### Fixed

- Fix keep dimensions when using only width or height
- Fix title, alt, align, and caption attributes
- Fixed pdf, and epub backend rendering

## [0.1.3] - [2018-12-18]

### Added

- Start adding tests to extension

### Fixed

- Now images are compatible with any backend

### Removed

- Groovy templates related code, now relying on Asciidoctor's image AST
- Unused constants

## [0.1.2] - [2018-12-16]

### Added

- Default width and height
- Error log when something fails while creating images

### Fixed

- Make image size match block attributes width

## [0.1.1] - [2018-12-15]

### Fixed

- Improved resolution based on `images` and `imagesoutdir` configuration entries

## [0.1.0] - [2018-12-15]

First release

### Added

- Extension
- Block processor


