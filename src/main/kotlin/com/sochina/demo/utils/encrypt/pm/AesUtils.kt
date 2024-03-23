package com.sochina.demo.utils.encrypt.pm

import com.sochina.demo.utils.Base64Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESUtils {
    private val LOGGER: Logger = LoggerFactory.getLogger(AESUtils::class.java)

    // 编码
    private const val ENCODING = "UTF-8"

    // 算法定义
    private const val AES_ALGORITHM = "AES"

    // 指定填充方式
    private const val CIPHER_PADDING = "AES/ECB/PKCS5Padding"
    private const val CIPHER_CBC_PADDING = "AES/CBC/PKCS5Padding"

    // 偏移量(CBC中使用，增强加密算法强度)
    private const val IV_SEED = "1234567812345678"

    /**
     * AES加密_ECB
     *
     * @param content 待加密内容
     * @param aesKey  密码
     * @return
     */
    fun encryptEcb(aesKey: String, content: String?): String? {
        if (content.isNullOrBlank()) {
            LOGGER.info("AES_ECB encrypt: the content is null!")
            return null
        }
        // 判断秘钥是否为16位
        if (aesKey.isNotBlank() && aesKey.length == 16) {
            try {
                // 对密码进行编码
                val bytes = aesKey.toByteArray(charset(ENCODING))
                // 设置加密算法，生成秘钥
                val skeySpec = SecretKeySpec(bytes, AES_ALGORITHM)
                // "算法/模式/补码方式"
                val cipher = Cipher.getInstance(CIPHER_PADDING)
                // 选择加密
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
                // 根据待加密内容生成字节数组
                val encrypted = cipher.doFinal(content.toByteArray(charset(ENCODING)))
                // 返回base64字符串
                return Base64Utils.encode(encrypted)
            } catch (e: Exception) {
                LOGGER.info("AES_ECB encrypt exception:" + e.message)
                throw Exception(e.message)
            }
        } else {
            LOGGER.info("AES_ECB encrypt: the aesKey is null or error!")
            return null
        }
    }

    /**
     * AES解密_ECB
     *
     * @param content 待解密内容
     * @param aesKey  密码
     * @return
     */
    fun decryptEcb(aesKey: String, content: String?): String? {
        if (content.isNullOrBlank()) {
            LOGGER.info("AES_ECB decrypt: the content is null!")
            return null
        }
        // 判断秘钥是否为16位
        if (aesKey.isNotBlank() && aesKey.length == 16) {
            try {
                // 对密码进行编码
                val bytes = aesKey.toByteArray(charset(ENCODING))
                // 设置解密算法，生成秘钥
                val skeySpec = SecretKeySpec(bytes, AES_ALGORITHM)
                // "算法/模式/补码方式"
                val cipher = Cipher.getInstance(CIPHER_PADDING)
                // 选择解密
                cipher.init(Cipher.DECRYPT_MODE, skeySpec)
                // 先进行Base64解码
                val decodeBase64: ByteArray = Base64Utils.decode(content)!!
                val decrypted = cipher.doFinal(decodeBase64)
                // 将字节数组转成字符串
                return String(decrypted, charset(ENCODING))
            } catch (e: Exception) {
                LOGGER.info("AES_ECB decrypt exception:" + e.message)
                throw Exception(e.message)
            }
        } else {
            LOGGER.info("AES_ECB decrypt: the aesKey is null or error!")
            return null
        }
    }

    /**
     * AES_CBC加密
     *
     * @param content 待加密内容
     * @param aesKey  密码
     * @return
     */
    fun encryptCbc(aesKey: String, content: String?): String? {
        if (content.isNullOrBlank()) {
            LOGGER.info("AES_CBC encrypt: the content is null!")
            return null
        }
        // 判断秘钥是否为16位
        if (aesKey.isNotBlank() && aesKey.length == 16) {
            try {
                // 对密码进行编码
                val bytes = aesKey.toByteArray(charset(ENCODING))
                // 设置加密算法，生成秘钥
                val skeySpec = SecretKeySpec(bytes, AES_ALGORITHM)
                // "算法/模式/补码方式"
                val cipher = Cipher.getInstance(CIPHER_CBC_PADDING)
                // 偏移
                val iv = IvParameterSpec(IV_SEED.toByteArray(charset(ENCODING)))
                // 选择加密
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
                // 根据待加密内容生成字节数组
                val encrypted = cipher.doFinal(content.toByteArray(charset(ENCODING)))
                // 返回base64字符串
                return Base64Utils.encode(encrypted)
            } catch (e: Exception) {
                LOGGER.info("AES_CBC encrypt exception:" + e.message)
                throw Exception(e.message)
            }
        } else {
            LOGGER.info("AES_CBC encrypt: the aesKey is null or error!")
            return null
        }
    }

    /**
     * AES_CBC解密
     *
     * @param content 待解密内容
     * @param aesKey  密码
     * @return
     */
    fun decryptCbc(aesKey: String, content: String?): String? {
        if (content.isNullOrBlank()) {
            LOGGER.info("AES_CBC decrypt: the content is null!")
            return null
        }
        // 判断秘钥是否为16位
        if (aesKey.isNotBlank() && aesKey.length == 16) {
            try {
                // 对密码进行编码
                val bytes = aesKey.toByteArray(charset(ENCODING))
                // 设置解密算法，生成秘钥
                val skeySpec = SecretKeySpec(bytes, AES_ALGORITHM)
                // 偏移
                val iv = IvParameterSpec(IV_SEED.toByteArray(charset(ENCODING)))
                // "算法/模式/补码方式"
                val cipher = Cipher.getInstance(CIPHER_CBC_PADDING)
                // 选择解密
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
                // 先进行Base64解码
                val decodeBase64: ByteArray = Base64Utils.decode(content)!!
                val decrypted = cipher.doFinal(decodeBase64)
                // 将字节数组转成字符串
                return String(decrypted, charset(ENCODING))
            } catch (e: Exception) {
                LOGGER.info("AES_CBC decrypt exception:" + e.message)
                throw Exception(e.message)
            }
        } else {
            LOGGER.info("AES_CBC decrypt: the aesKey is null or error!")
            return null
        }
    }
}
