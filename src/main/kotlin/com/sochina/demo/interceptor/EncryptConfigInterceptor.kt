package com.sochina.demo.interceptor

import com.sochina.demo.utils.encrypt.gm.sm4.SM4Utils
import io.smallrye.config.ConfigSourceInterceptor
import io.smallrye.config.ConfigSourceInterceptorContext
import io.smallrye.config.ConfigValue
import io.smallrye.config.Priorities
import jakarta.annotation.Priority


@Priority(value = Priorities.PLATFORM)
class EncryptConfigInterceptor : ConfigSourceInterceptor {
    override fun getValue(context: ConfigSourceInterceptorContext, name: String): ConfigValue? {
        val key = System.getProperty("gs4k")
        val ePrefix = System.getProperty("gs4kp")
        if (key == null || key.isEmpty()) {
            throw RuntimeException("key or prefix is null")
        }
        val config: ConfigValue? = context.proceed(name)
        return config?.value?.let { value ->
            if (value.startsWith(ePrefix)) {
                val encryptValue = value.removePrefix(ePrefix)
                val decryptedValue = SM4Utils.decryptCbc(key, encryptValue)
                config.withValue(decryptedValue)
            } else {
                config
            }
        } ?: config
    }
}