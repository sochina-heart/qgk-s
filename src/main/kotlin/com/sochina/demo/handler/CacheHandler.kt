package com.sochina.demo.handler

import io.quarkus.cache.CacheInvalidate
import io.quarkus.cache.CacheKey
import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CacheHandler {

    @CacheResult(cacheName = "sochinaToken")
    fun cacheToken(@CacheKey token: String): String = token

    @CacheResult(cacheName = "sochinaPerms")
    fun getCachePerms(@CacheKey token: String) = emptyList<String>()

    @CacheInvalidate(cacheName = "sochinaPerms")
    fun invalidatePerms(@CacheKey token: String){}

    @CacheInvalidate(cacheName = "sochinaRouter")
    fun invalidateRouter(@CacheKey token: String){}

    @CacheInvalidate(cacheName = "sochinaToken")
    fun invalidateToken(@CacheKey token: String){}

    fun invalidateUserCache(token: String) {
        invalidatePerms(token)
        invalidateRouter(token)
        invalidateToken(token)
    }
}