div(class: 'imageblock') {
    div(class: 'title') {
        yieldUnescaped title
    }
    div(class: 'content') {
        img(src: imagePath.absolutePath, alt: alt, width: width ?: 100, height: height ?: 50)
    }
}