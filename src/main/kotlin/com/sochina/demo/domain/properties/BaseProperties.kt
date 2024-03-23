package com.sochina.demo.domain.properties

import java.io.Serializable

/**
 * @author sochina-heart
 */
open class BaseProperties : Serializable {

    var enable: Boolean = true

    var excludeUrl: MutableList<String>? = null;

    var order: Int = -1
}