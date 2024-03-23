package com.sochina.demo.utils.encrypt.pm

import com.sochina.demo.utils.Base64Utils
import org.apache.commons.io.FileUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

object RSAUtils {
    /**
     * 加密算法RSA
     */
    private const val KEY_ALGORITHM = "RSA"

    /**
     * 签名算法
     */
    private const val SIGNATURE_ALGORITHM = "MD5withRSA"

    /**
     * 获取公钥的key
     */
    private const val PUBLIC_KEY = "RSAPublicKey"

    /**
     * 获取私钥的key
     */
    private const val PRIVATE_KEY = "RSAPrivateKey"

    /**
     * RSA 密钥位数
     */
    private const val KEY_SIZE = 1024

    /**
     * RSA最大解密密文大小
     */
    private const val MAX_DECRYPT_BLOCK = KEY_SIZE / 8

    /**
     * RSA最大加密明文大小
     */
    private const val MAX_ENCRYPT_BLOCK = MAX_DECRYPT_BLOCK - 11

    /**
     * 生成密钥对(公钥和私钥)
     */
    @Throws(Exception::class)
    fun genKeyPair(): Map<String, Any> {
        val keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM)
        keyPairGen.initialize(KEY_SIZE)
        val keyPair = keyPairGen.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val keyMap: MutableMap<String, Any> = HashMap(2)
        keyMap[PUBLIC_KEY] = publicKey
        keyMap[PRIVATE_KEY] = privateKey
        return keyMap
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     */
    @Throws(Exception::class)
    fun sign(privateKey: String, data: String): String? {
        val keyBytes: ByteArray? = Base64Utils.decode(privateKey)
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK = keyFactory.generatePrivate(pkcs8KeySpec)
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initSign(privateK)
        signature.update(data.toByteArray())
        return Base64Utils.encode(signature.sign())
    }

    /**
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     */
    @Throws(Exception::class)
    fun verify(publicKey: String, data: String, sign: String?): Boolean {
        val keyBytes: ByteArray? = Base64Utils.decode(publicKey)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK = keyFactory.generatePublic(keySpec)
        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initVerify(publicK)
        signature.update(data.toByteArray())
        return signature.verify(Base64Utils.decode(sign))
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     */
    @Throws(Exception::class)
    private fun decryptByPrivateKey(privateKey: String, encryptedData: ByteArray): ByteArray {
        val keyBytes: ByteArray? = Base64Utils.decode(privateKey)
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK: Key = keyFactory.generatePrivate(pkcs8KeySpec)
        val cipher = Cipher.getInstance(keyFactory.algorithm)
        cipher.init(Cipher.DECRYPT_MODE, privateK)
        return init(encryptedData, cipher)
    }

    /**
     * 公钥解密
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     */
    @Throws(Exception::class)
    private fun decryptByPublicKey(publicKey: String, encryptedData: ByteArray): ByteArray {
        val keyBytes: ByteArray? = Base64Utils.decode(publicKey)
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK: Key = keyFactory.generatePublic(x509KeySpec)
        val cipher = Cipher.getInstance(keyFactory.algorithm)
        cipher.init(Cipher.DECRYPT_MODE, publicK)
        return init(encryptedData, cipher)
    }

    @Throws(Exception::class)
    private fun init(data: ByteArray, cipher: Cipher): ByteArray {
        val inputLen = data.size
        val out = ByteArrayOutputStream()
        var offSet = 0
        var cache: ByteArray
        var i = 0
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            cache = if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK)
            } else {
                cipher.doFinal(data, offSet, inputLen - offSet)
            }
            out.write(cache, 0, cache.size)
            i++
            offSet = i * MAX_DECRYPT_BLOCK
        }
        val result = out.toByteArray()
        out.close()
        return result
    }

    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     */
    @Throws(Exception::class)
    private fun encryptByPublicKey(publicKey: String, data: ByteArray): ByteArray {
        val keyBytes: ByteArray? = Base64Utils.decode(publicKey)
        val x509KeySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val publicK: Key = keyFactory.generatePublic(x509KeySpec)
        // 对数据加密
        val cipher = Cipher.getInstance(keyFactory.algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, publicK)
        return init(data, cipher)
    }

    /**
     * 私钥加密
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     */
    @Throws(Exception::class)
    private fun encryptByPrivateKey(privateKey: String, data: ByteArray): ByteArray {
        val keyBytes: ByteArray? = Base64Utils.decode(privateKey)
        val pkcs8KeySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(KEY_ALGORITHM)
        val privateK: Key = keyFactory.generatePrivate(pkcs8KeySpec)
        val cipher = Cipher.getInstance(keyFactory.algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, privateK)
        return init(data, cipher)
    }

    /**
     * 获取私钥
     *
     * @param keyMap 密钥对
     */
    @Throws(Exception::class)
    fun getPrivateKey(keyMap: Map<String?, Any?>): String? {
        val key = keyMap[PRIVATE_KEY] as Key?
        return Base64Utils.encode(key!!.encoded)
    }

    /**
     * 获取公钥
     *
     * @param keyMap 密钥对
     */
    @Throws(Exception::class)
    fun getPublicKey(keyMap: Map<String?, Any?>): String? {
        val key = keyMap[PUBLIC_KEY] as Key?
        return Base64Utils.encode(key!!.encoded)
    }

    /**
     * 生成公私钥文件
     *
     * @param publicFilePath  存储公钥的路径和文件名,例如：D:/pub/public.key
     * @param privateFilePath 存储私钥的路径和文件名,例如：D:/pri/private.key
     */
    @Throws(Exception::class)
    fun createKey(publicFilePath: String, privateFilePath: String, keySize: Int) {
        // 生成密钥对
        val keyGen = KeyPairGenerator.getInstance(KEY_ALGORITHM)
        keyGen.initialize(keySize, SecureRandom())
        val pair = keyGen.generateKeyPair()
        // 将已编码的密钥（公钥和私钥）字节写到文件中
        write(publicFilePath, pair.public)
        write(privateFilePath, pair.private)
    }

    /**
     * 写入一个对象
     *
     * @param path 路径
     * @param key  密钥对象
     */
    @Throws(Exception::class)
    private fun write(path: String, key: Any) {
        val file = File(path)
        if (!file.parentFile.exists()) {
            // 创建文件目录
            val create = file.parentFile.mkdirs()
            if (!create) {
                return
            }
        }
        try {
            ObjectOutputStream(Files.newOutputStream(Paths.get(path))).use { oos ->
                oos.writeObject(key)
            }
        } catch (e: Exception) {
            throw Exception("密钥写入异常", e)
        }
    }

    /**
     * 根据公钥文件存放位置解析为公钥对象
     *
     * @param path 存放位置，例如：D:/pub/public.key
     */
    @Throws(Exception::class)
    private fun resolvePublicKey(path: String): PublicKey {
        FileUtils.openInputStream(File(path)).use { fis ->
            ObjectInputStream(fis).use { ois ->
                return ois.readObject() as PublicKey
            }
        }
    }

    /**
     * 根据私钥文件存放位置解析为私钥对象
     *
     * @param path 存放位置，例如：D:/pri/private.key
     */
    @Throws(Exception::class)
    private fun resolvePrivateKey(path: String): PrivateKey {
        FileUtils.openInputStream(File(path)).use { fis ->
            ObjectInputStream(fis).use { ois ->
                return ois.readObject() as PrivateKey
            }
        }
    }

    /**
     * 将传入的公钥对象转换为经过Base64编码后的公钥字符串
     *
     * @param pubKey
     */
    private fun getBase64PublicKeyString(pubKey: PublicKey): String {
        return Base64Utils.encode(pubKey.encoded)!!.trim()
    }

    /**
     * 将传入的私钥对象转换为经过Base64编码后的私钥字符串
     *
     * @param priKey
     */
    private fun getBase64PrivateKeyString(priKey: PrivateKey): String {
        return Base64Utils.encode(priKey.encoded)!!.trim()
    }

    /**
     * 将传入的公钥存储路径读取公钥后转换为经过Base64编码后的公钥字符串
     *
     * @param path 存放位置，例如：D:/pub/public.key
     */
    @Throws(Exception::class)
    fun getBase64PublicKeyString(path: String): String {
        val pubKey = resolvePublicKey(path)
        return getBase64PublicKeyString(pubKey)
    }

    /**
     * 将传入的私钥存储路径读取私钥后转换为经过Base64编码后的私钥字符串
     *
     * @param path 存放位置，例如：D:/pri/private.key
     */
    @Throws(Exception::class)
    fun getBase64PrivateKeyString(path: String): String {
        val priKey = resolvePrivateKey(path)
        return getBase64PrivateKeyString(priKey)
    }

    /**
     * server端公钥加密
     */
    @Throws(Exception::class)
    fun encryptedDataOnServer(data: String?, publicKey: String): String? {
        return if (data.isNullOrBlank()) {
            null
        } else {
            Base64Utils.encode(encryptByPublicKey(publicKey, data.toByteArray()))
        }
    }

    /**
     * server端私钥解密
     */
    @Throws(Exception::class)
    fun decryptDataOnServer(data: String?, privateKey: String): String? {
        return if (data.isNullOrBlank()) {
            null
        } else {
            String(decryptByPrivateKey(privateKey, Base64Utils.decode(data)!!), StandardCharsets.UTF_8)
        }
    }
}
