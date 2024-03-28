package com.sochina.demo.domain.properties

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CommonsProperties {

    var xssProperties = XssProperties()
}