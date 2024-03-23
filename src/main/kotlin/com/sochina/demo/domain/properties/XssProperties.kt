package com.sochina.demo.domain.properties

import java.io.Serializable

/**
 * @author sochina-heart
 */
class XssProperties : BaseProperties(), Serializable {
    var sensitiveData: String = ""
    var pattern: MutableMap<String, String>? = mutableMapOf()
}