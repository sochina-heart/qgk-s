package com.sochina.demo.domain.properties

/**
 * @author sochina-heart
 */
class XssProperties : BaseProperties() {
    var sensitiveData: String = ""
    var pattern: Map<String, String> = emptyMap()
}