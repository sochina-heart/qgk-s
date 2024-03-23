package com.sochina.demo.domain.properties

import jakarta.enterprise.context.ApplicationScoped
import java.io.Serializable

@ApplicationScoped
class CommonsProperties : Serializable {

    var xssProperties = XssProperties()
}