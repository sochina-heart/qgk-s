package com.sochina.demo.utils.encrypt.gm

import org.bouncycastle.crypto.digests.SM3Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.io.UnsupportedEncodingException
import java.security.Security

object SM3Utils {
    private const val ENCODING = "UTF-8"

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    /**
     * sm3算法加密
     *
     * @param paramStr 待加密字符串
     * @return 返回加密后，固定长度=32的16进制字符串
     */
    fun encrypt(paramStr: String): String {
        // 将返回的hash值转换成16进制字符串
        var resultHexString = ""
        try {
            // 将字符串转换成byte数组
            val srcData = paramStr.toByteArray(charset(ENCODING))
            // 调用hash()
            val resultHash = hash(srcData)
            // 将返回的hash值转换成16进制字符串
            resultHexString = Hex.toHexString(resultHash)
        } catch (e: UnsupportedEncodingException) {
            throw Exception(e.message)
        }
        return resultHexString
    }

    /**
     * 返回长度=32的byte数组
     *
     * @param srcData
     * @return 生成对应的hash值
     */
    private fun hash(srcData: ByteArray): ByteArray {
        val digest = SM3Digest()
        digest.update(srcData, 0, srcData.size)
        val hash = ByteArray(digest.digestSize)
        digest.doFinal(hash, 0)
        return hash
    }

    /**
     * sm3算法加密
     *
     * @param key      密钥
     * @param paramStr 待加密字符串
     * @return 返回加密后，固定长度=32的16进制字符串
     */
    fun encryptPlus(key: String, paramStr: String): String {
        // 将返回的hash值转换成16进制字符串
        var resultHexString = ""
        try {
            // 将字符串转换成byte数组
            val srcData = paramStr.toByteArray(charset(ENCODING))
            // 调用hash()
            val resultHash = hmac(key.toByteArray(charset(ENCODING)), srcData)
            // 将返回的hash值转换成16进制字符串
            resultHexString = Hex.toHexString(resultHash)
        } catch (e: UnsupportedEncodingException) {
            throw Exception(e.message)
        }
        return resultHexString
    }

    /**
     * 通过密钥进行加密
     * 被加密的byte数组
     *
     * @param key     密钥
     * @param srcData
     * @return 指定密钥进行加密
     */
    private fun hmac(key: ByteArray, srcData: ByteArray): ByteArray {
        val keyParameter = KeyParameter(key)
        val digest = SM3Digest()
        val mac = HMac(digest)
        mac.init(keyParameter)
        mac.update(srcData, 0, srcData.size)
        val result = ByteArray(mac.macSize)
        mac.doFinal(result, 0)
        return result
    }

    /**
     * 判断源数据与加密数据是否一致
     *
     * @param srcStr       原字符串
     * @param sm3HexString 16进制字符串
     * @return 校验结果
     * 通过验证原数组和生成的hash数组是否为同一数组，验证2者是否为同一数据
     */
    fun verify(srcStr: String, sm3HexString: String?): Boolean {
        var flag = false
        try {
            val srcData = srcStr.toByteArray(charset(ENCODING))
            val sm3Hash = Hex.decode(sm3HexString)
            val newHash = hash(srcData)
            if (newHash.contentEquals(sm3Hash)) {
                flag = true
            }
        } catch (e: UnsupportedEncodingException) {
            throw Exception(e.message)
        }
        return flag
    }

    /**
     * 判断源数据与加密数据是否一致-秘钥
     *
     * @param srcStr       原字符串
     * @param sm3HexString 16进制字符串
     * @return 校验结果
     * 通过验证原数组和生成的hash数组是否为同一数组，验证2者是否为同一数据
     */
    fun verifyPlus(key: String, srcStr: String, sm3HexString: String?): Boolean {
        var flag = false
        try {
            val srcData = srcStr.toByteArray(charset(ENCODING))
            val sm3Hash = Hex.decode(sm3HexString)
            val keyBytes = key.toByteArray(charset(ENCODING))
            val hmac = hmac(keyBytes, srcData)
            if (hmac.contentEquals(sm3Hash)) {
                flag = true
            }
        } catch (e: UnsupportedEncodingException) {
            throw Exception(e.message)
        }
        return flag
    }
}
