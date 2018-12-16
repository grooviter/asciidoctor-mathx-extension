package org.asciidoctor.mathx

import groovy.text.Template

/**
 * This class contains all constants used in this Asciidoctor extension
 *
 * @since 0.1.2
 */
class Constants {

    /**
     * Default image's width
     *
     * @since 0.1.2
     */
    static final Integer DEFAULT_WIDTH = 500

    /**
     * Default image's height
     *
     * @since 0.1.2
     */
    static final Integer DEFAULT_HEIGHT = 250

    /**
     * Empty {@link Map}
     *
     * @since 0.1.2
     */
    static final Map<String, Object> EMPTY_MAP_ST_OBJ = [:] as Map<String, Object>

    /**
     * Empty {@link Map}
     *
     * @since 0.1.2
     */
    static final Map<Object, Object> EMPTY_MAP_OBJ_OBJ = [:] as Map<Object, Object>

    /**
     * Template for an image block with title
     *
     * @since 0.1.2
     */
    static final Template TEMPLATE = Utils.createTemplate('template.tpl')

    /**
     * Template for an image block without title
     *
     * @since 0.1.2
     */
    static final Template TEMPLATE_NO_TITLE = Utils.createTemplate('templateNoTitle.tpl')
}
