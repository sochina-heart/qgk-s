package com.sochina.demo.utils.encrypt.gm.sm4

import com.sochina.demo.utils.Base64Utils
import com.sochina.demo.utils.encrypt.gm.SM3Utils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Hex
import java.security.AlgorithmParameters
import java.security.Key
import java.security.SecureRandom
import java.security.Security
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

object SM4Utils {
    private val ENCODING: String = Sm4Helper.ENCODING.msg
    private val ALGORITHM_NAME: String = Sm4Helper.ALGORITHM_NAME.msg
    private val ALGORITHM_NAME_CBC_PADDING: String = Sm4Helper.ALGORITHM_NAME_CBC_PADDING.msg

    // 加密算法/分组加密模式/分组填充方式
    // PKCS5Padding-以8个字节为一组进行分组加密
    // 定义分组加密模式使用：PKCS5Padding
    private val ALGORITHM_NAME_ECB_PADDING: String = Sm4Helper.ALGORITHM_NAME_ECB_PADDING.msg

    // 128-32位16进制；256-64位16进制
    private const val DEFAULT_KEY_SIZE = 128
    private const val KEY = "bd74842f72ddbce7751dccf708a37c8f"

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    @Throws(Exception::class)
    fun generateKeyString(): String {
        return Hex.toHexString(generateKey())
    }

    /**
     * @param keySize
     */
    /**
     * 自动生成密钥
     */
    @Throws(Exception::class)
    private fun generateKey(keySize: Int = DEFAULT_KEY_SIZE): ByteArray {
        val kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME)
        kg.init(keySize, SecureRandom())
        return kg.generateKey().encoded
    }

    /**
     * sm4加密_CBC
     *
     * @param hexKey   16进制密钥（忽略大小写）
     * @param paramStr 待加密字符串
     * 返回16进制的加密字符串
     *
     *
     * 加密模式：CBC
     */
    @Throws(Exception::class)
    fun encryptCbc(hexKey: String?, paramStr: String?): String {
        var result = ""
        // 16进制字符串-->byte[]
        val keyData = Hex.decode(hexKey)
        // String-->byte[]
        val srcData = paramStr!!.toByteArray(charset(ENCODING))
        // 加密后的数组
        val cipherArray = encryptCbcPadding(keyData, srcData)
        // byte[]-->hexString
        result = Hex.toHexString(cipherArray)
        return result
    }

    /**
     * sm4加密_ECB
     *
     * @param hexKey   16进制密钥（忽略大小写）
     * @param paramStr 待加密字符串
     * 返回16进制的加密字符串
     *
     *
     * 加密模式：CBC
     */
    @Throws(Exception::class)
    fun encryptEcb(hexKey: String?, paramStr: String?): String {
        var result = ""
        // 16进制字符串-->byte[]
        val keyData = Hex.decode(hexKey)
        // String-->byte[]
        val srcData = paramStr!!.toByteArray(charset(ENCODING))
        // 加密后的数组
        val cipherArray = encryptEcbPadding(keyData, srcData)
        // byte[]-->hexString
        result = Hex.toHexString(cipherArray)
        return result
    }

    /**
     * 加密模式之CBC
     *
     * @param key
     * @param data
     */
    @Throws(Exception::class)
    private fun encryptCbcPadding(key: ByteArray, data: ByteArray): ByteArray {
        val cipher = generateCbcCipher(ALGORITHM_NAME_CBC_PADDING, Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data)
    }

    @Throws(Exception::class)
    private fun generateCbcCipher(algorithmName: String, mode: Int, key: ByteArray): Cipher {
        val cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME)
        val sm4Key: Key = SecretKeySpec(key, ALGORITHM_NAME)
        cipher.init(mode, sm4Key, generateIV())
        return cipher
    }

    /**
     * 加密模式之ECB
     *
     * @param key
     * @param data
     */
    @Throws(Exception::class)
    private fun encryptEcbPadding(key: ByteArray, data: ByteArray): ByteArray {
        val cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data)
    }

    @Throws(Exception::class)
    private fun generateEcbCipher(algorithmName: String, mode: Int, key: ByteArray): Cipher {
        val cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME)
        val sm4Key: Key = SecretKeySpec(key, ALGORITHM_NAME)
        cipher.init(mode, sm4Key)
        return cipher
    }

    // 生成iv
    @Throws(Exception::class)
    private fun generateIV(): AlgorithmParameters {
        // iv 为一个 16 字节的数组，这里采用和 iOS 端一样的构造方法，数据全为0
        val iv = ByteArray(16)
        Arrays.fill(iv, 0x00.toByte())
        val params = AlgorithmParameters.getInstance(ALGORITHM_NAME)
        params.init(IvParameterSpec(iv))
        return params
    }

    /**
     * sm4解密_CBC
     *
     * @param hexKey 16进制密钥
     * @param text   16进制的加密字符串（忽略大小写）
     * 解密后的字符串
     *
     *
     * 解密模式：采用CBC
     */
    @Throws(Exception::class)
    fun decryptCbc(hexKey: String?, text: String?): String {
        // 用于接收解密后的字符串
        var result = ""
        // hexString-->byte[]
        val keyData = Hex.decode(hexKey)
        // hexString-->byte[]
        val resultData = Hex.decode(text)
        // 解密
        val srcData = decryptCbcPadding(keyData, resultData)
        // byte[]-->String
        result = String(srcData, charset(ENCODING))
        return result
    }

    /**
     * sm4解密_ECB
     *
     * @param hexKey 16进制密钥
     * @param text   16进制的加密字符串（忽略大小写）
     * 解密后的字符串
     *
     *
     * 解密模式：采用ECB
     */
    @Throws(Exception::class)
    fun decryptEcb(hexKey: String?, text: String?): String {
        // 用于接收解密后的字符串
        var result = ""
        // hexString-->byte[]
        val keyData = Hex.decode(hexKey)
        // hexString-->byte[]
        val resultData = Hex.decode(text)
        // 解密
        val srcData = decryptEcbPadding(keyData, resultData)
        // byte[]-->String
        result = String(srcData, charset(ENCODING))
        return result
    }

    /**
     * 解密
     *
     * @param key
     * @param cipherText
     */
    @Throws(Exception::class)
    private fun decryptCbcPadding(key: ByteArray, cipherText: ByteArray): ByteArray {
        val cipher = generateCbcCipher(ALGORITHM_NAME_CBC_PADDING, Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(cipherText)
    }

    /**
     * 解密
     *
     * @param key
     * @param cipherText
     */
    @Throws(Exception::class)
    private fun decryptEcbPadding(key: ByteArray, cipherText: ByteArray): ByteArray {
        val cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key)
        return cipher.doFinal(cipherText)
    }

    @Throws(Exception::class)
    fun encryptCbc(paramStr: String?): String? {
        return if (paramStr.isNullOrBlank()) {
            null
        } else {
            encryptCbc(KEY, paramStr)
        }
    }

    @Throws(Exception::class)
    fun decryptCbc(paramStr: String?): String? {
        return if (paramStr.isNullOrBlank()) {
            null
        } else {
            decryptCbc(KEY, paramStr)
        }
    }

    @Throws(Exception::class)
    fun encryptEcb(paramStr: String?): String? {
        return if (paramStr.isNullOrBlank()) {
            return null
        } else {
            encryptEcb(KEY, paramStr)
        }
    }

    @Throws(Exception::class)
    fun decryptEcb(paramStr: String?): String? {
        return if (paramStr.isNullOrBlank()) {
            null
        } else {
            decryptEcb(KEY, paramStr)
        }
    }

    private const val KEYSTR = "1234567890123456"
    const val IVSTR = "1234567890123456"

    fun encrypt(data: String): String? {
        try {
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val blockSize = cipher.blockSize
            val dataBytes = data.toByteArray()
            var plaintextLength = dataBytes.size
            if (plaintextLength % blockSize != 0) {
                plaintextLength += (blockSize - (plaintextLength % blockSize))
            }
            val plaintext = ByteArray(plaintextLength)
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.size)
            val keyspec = SecretKeySpec(KEYSTR.toByteArray(), "AES")
            val ivspec = IvParameterSpec(IVSTR.toByteArray())
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec)
            val encrypted = cipher.doFinal(plaintext)
            return org.bouncycastle.util.encoders.Base64.toBase64String(encrypted)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return "";
        }
    }

    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @param key  解密key
     * @param iv   解密iv
     * @return 解密的结果
     */
    fun desEncrypt(data: String?): String {
        try {
            val encrypted1: ByteArray? = Base64Utils.decode(data)
            val cipher = Cipher.getInstance("AES/CBC/NoPadding")
            val keySpec = SecretKeySpec(KEYSTR.toByteArray(), "AES")
            val ivSpec = IvParameterSpec(IVSTR.toByteArray())
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val original = cipher.doFinal(encrypted1)
            return String(original).trim { it <= ' ' }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun encryptPass(password: String, salt: String): String {
        val key = SM3Utils.encryptPlus(salt, password)
        return encryptCbc(key)!!
    }

    fun checkPassword(password: String?, salt: String?, encryptPassword: String?): Boolean {
        if (encryptPassword.isNullOrBlank() || password.isNullOrBlank() || salt.isNullOrBlank()) {
            return false
        }
        return encryptPass(password, salt) == encryptPassword
    }
}
