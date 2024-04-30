package com.sochina.demo.handler

import io.quarkus.cache.CacheKey
import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CacheHandler {

    @CacheResult(cacheName = "sochinaPerms")
    fun getCachePerms(@CacheKey token: String) = emptyList<String>()
}