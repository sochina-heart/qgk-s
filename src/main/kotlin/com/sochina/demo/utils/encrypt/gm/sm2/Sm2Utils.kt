package com.sochina.demo.utils.encrypt.gm.sm2

import com.sochina.demo.utils.encrypt.gm.sm2.SM2Helper.hash
import com.sochina.demo.utils.encrypt.gm.sm2.SM2Helper.toByteArray
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.math.ec.ECCurve
import org.bouncycastle.math.ec.ECPoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import kotlin.math.ceil

object SM2Utils {
    private val LOGGER: Logger = LoggerFactory.getLogger(SM2Utils::class.java)
    private const val DIGEST_LENGTH = 32

    // private static final String FWF_PUBLIC_KEY = "AlTPz/SfIhn85qZBiHTYprnneeUs049ECaaqEzZC/G4P";
    private const val PUBLIC_KEY = "PUBLIC_KEY"
    private const val PRIVATE_KEY = "PRIVATE_KEY"
    private const val PUBLIC_KEY_VALUE = "A5OHqeG/hRLZ0RL4qDhNG0gF6sleCxkT9OTJayS/L6ZN"
    private const val PRIVATE_KEY_VALUE = "bKMuRqbRa3au5h4pkhnxC7q4Je8Xv+yEzSzN+Qpz9rA="
    private val n = BigInteger(
        "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "7203DF6B" + "21C6052B" + "53BBF409" + "39D54123", 16
    )
    private val p = BigInteger(
        "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFF", 16
    )
    private val a = BigInteger(
        "FFFFFFFE" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "00000000" + "FFFFFFFF" + "FFFFFFFC", 16
    )
    private val b = BigInteger(
        "28E9FA9E" + "9D9F5E34" + "4D5A9E4B" + "CF6509A7" + "F39789F5" + "15AB8F92" + "DDBCBD41" + "4D940E93", 16
    )
    private val gx = BigInteger(
        "32C4AE2C" + "1F198119" + "5F990446" + "6A39C994" + "8FE30BBF" + "F2660BE1" + "715A4589" + "334C74C7", 16
    )
    private val gy = BigInteger(
        "BC3736A2" + "F4F6779C" + "59BDCEE3" + "6B692153" + "D0A9877C" + "C62A4740" + "02DF32E5" + "2139F0A0", 16
    )
    private var ECC_BC_SPEC: ECDomainParameters
    private val random = SecureRandom()
    private var curve = ECCurve.Fp(p, a, b, null, null)
    private var G: ECPoint = curve.createPoint(gx, gy)

    init {
        ECC_BC_SPEC = ECDomainParameters(curve, G, n)
    }

    fun generateKeyPairMap(): Map<String, Any>? {
        val keyMap: MutableMap<String, Any> = HashMap(4)
        val privateKey = random(n.subtract(BigInteger("1")))
        val publicKey = G.multiply(privateKey).normalize()
        return if (checkPublicKey(publicKey)) {
            keyMap[PUBLIC_KEY] = publicKey
            keyMap[PRIVATE_KEY] = privateKey
            keyMap
        } else {
            null
        }
    }

    fun getPublicKey(keyMap: Map<String?, Any?>): String {
        val publicKey = keyMap[PUBLIC_KEY] as ECPoint?
        val keyEncoded = publicKey!!.getEncoded(true)
        return encryptBase64(keyEncoded)
    }

    fun getPrivateKey(keyMap: Map<String?, Any?>): String {
        val privateKey = keyMap[PRIVATE_KEY] as BigInteger?
        val keyEncoded = privateKey!!.toByteArray()
        return encryptBase64(keyEncoded)
    }

    fun encrypt(publicKey: String, input: String): String {
        var encrypt: ByteArray? = null
        // var data = URLEncoder.encode(input, "utf-8");
        val data = input
        val keyByte = decryptBase64(publicKey)
        val point = curve.decodePoint(keyByte)
        encrypt = encrypt(data, point)
        // return Hex.toHexString(encrypt);  //16进制 字符串
        return encryptBase64(encrypt) // base64
    }

    private fun encrypt(input: String, publicKey: ECPoint): ByteArray {
        // byte[] inputBuffer = input.getBytes();
        val inputBuffer = input.toByteArray(StandardCharsets.UTF_8)
        var c1Buffer: ByteArray
        var kpb: ECPoint
        var t: ByteArray?
        do {
            val k = random(n)
            val c1 = G.multiply(k)
            c1Buffer = c1.getEncoded(false)
            val h = ECC_BC_SPEC.h
            if (h != null) {
                val s = publicKey.multiply(h)
                check(!s.isInfinity)
            }
            kpb = publicKey.multiply(k).normalize()
            val kpbBytes = kpb.getEncoded(false)
            t = kdf(kpbBytes, inputBuffer.size)
        } while (allZero(t))
        val c2 = ByteArray(inputBuffer.size) { (inputBuffer[it].toInt() xor t!![it].toInt()).toByte() }
        val c3 = sm3hash(
            kpb.xCoord.toBigInteger().toByteArray(), inputBuffer,
            kpb.yCoord.toBigInteger().toByteArray()
        )
        val encryptResult = ByteArray(c1Buffer.size + c2.size + c3!!.size)
        System.arraycopy(c1Buffer, 0, encryptResult, 0, c1Buffer.size)
        System.arraycopy(c2, 0, encryptResult, c1Buffer.size, c2.size)
        System.arraycopy(c3, 0, encryptResult, c1Buffer.size + c2.size, c3.size)
        return encryptResult
    }

    fun decrypt(privateKeyStr: String, input: String): String? {
        val keyByte = decryptBase64(privateKeyStr)
        val privateKey = BigInteger(keyByte)
        // byte[] enStr = Hex.decode(input); //16进制 字符串
        val enStr = decryptBase64(input) // base64
        return decrypt(enStr, privateKey)
    }

    private fun decrypt(encryptData: ByteArray, privateKey: BigInteger): String? {
        val c1Byte = ByteArray(65)
        System.arraycopy(encryptData, 0, c1Byte, 0, c1Byte.size)
        val c1 = curve.decodePoint(c1Byte).normalize()
        val h = ECC_BC_SPEC.h
        if (h != null) {
            val s = c1.multiply(h)
            check(!s.isInfinity)
        }
        val dBC1 = c1.multiply(privateKey).normalize()
        val dBC1Bytes = dBC1.getEncoded(false)
        val klen = encryptData.size - 65 - DIGEST_LENGTH
        val t = kdf(dBC1Bytes, klen)
        if (allZero(t)) {
            LOGGER.error("all zero")
            throw IllegalStateException()
        }
        val M = ByteArray(klen)
        for (i in M.indices) {
            M[i] = (encryptData[c1Byte.size + i].toInt() xor t!![i].toInt()).toByte()
        }
        val C3 = ByteArray(DIGEST_LENGTH)
        System.arraycopy(encryptData, encryptData.size - DIGEST_LENGTH, C3, 0, DIGEST_LENGTH)
        val u = sm3hash(
            dBC1.xCoord.toBigInteger().toByteArray(), M,
            dBC1.yCoord.toBigInteger().toByteArray()
        )
        return if (u.contentEquals(C3)) {
            String(M, StandardCharsets.UTF_8)
        } else {
            null
        }
    }

    private fun between(param: BigInteger, min: BigInteger, max: BigInteger): Boolean {
        return param >= min && param < max
    }

    private fun checkPublicKey(publicKey: ECPoint): Boolean {
        if (!publicKey.isInfinity) {
            val x = publicKey.xCoord.toBigInteger()
            val y = publicKey.yCoord.toBigInteger()
            if (between(x, BigInteger("0"), p) && between(y, BigInteger("0"), p)) {
                val xResult = x.pow(3).add(a.multiply(x)).add(b).mod(p)
                val yResult = y.pow(2).mod(p)
                return yResult == xResult && publicKey.multiply(n).isInfinity
            }
        }
        return false
    }

    private fun join(vararg params: ByteArray?): ByteArray? {
        val baos = ByteArrayOutputStream()
        params.forEach { it?.let { item -> baos.write(item) } }
        return baos.toByteArray()
    }

    private fun sm3hash(vararg params: ByteArray?): ByteArray? {
        return hash(join(*params)!!)
    }

    private fun ZA(IDA: String, aPublicKey: ECPoint): ByteArray? {
        val idaBytes = IDA.toByteArray()
        val entlenA = idaBytes.size * 8
        val entla = byteArrayOf((entlenA and 0xFF00).toByte(), (entlenA and 0x00FF).toByte())
        return sm3hash(
            entla, idaBytes, a.toByteArray(), b.toByteArray(), gx.toByteArray(), gy.toByteArray(),
            aPublicKey.xCoord.toBigInteger().toByteArray(),
            aPublicKey.yCoord.toBigInteger().toByteArray()
        )
    }

    private fun sign(M: String, signFlag: String, publicKey: ECPoint, privateKey: BigInteger): String {
        val ZA = ZA(signFlag, publicKey)
        val M_ = join(ZA!!, M.toByteArray())
        val e = BigInteger(1, sm3hash(M_))
        var k: BigInteger
        var r: BigInteger
        do {
            k = random(n)
            val p1 = G.multiply(k).normalize()
            val x1 = p1.xCoord.toBigInteger()
            r = e.add(x1)
            r = r.mod(n)
        } while (r == BigInteger.ZERO || r.add(k) == n)
        val s = privateKey.add(BigInteger.ONE).modInverse(n)
            .multiply(k.subtract(r.multiply(privateKey)).mod(n)).mod(n)
        val rBytes = r.toByteArray()
        val sBytes = s.toByteArray()
        val rBase64String = encryptBase64(rBytes)
        val sBase64String = encryptBase64(sBytes)
        return "$rBase64String,$sBase64String"
    }

    private fun kdf(Z: ByteArray, klen: Int): ByteArray? {
        var ct = 1
        val end = ceil(klen * 1.0 / 32).toInt()
        val baos = ByteArrayOutputStream()
        for (i in 1 until end) {
            sm3hash(Z, toByteArray(ct))?.let { baos.write(it) }
            ct++
        }
        val last = sm3hash(Z, toByteArray(ct))
        if (klen % 32 == 0) {
            baos.write(last)
        } else {
            baos.write(last, 0, klen % 32)
        }
        return baos.toByteArray()
    }

    private fun encryptBase64(key: ByteArray?): String {
        return String(Base64.getEncoder().encode(key), StandardCharsets.UTF_8)
    }

    private fun decryptBase64(key: String): ByteArray {
        return try {
            Base64.getDecoder().decode(key.toByteArray(StandardCharsets.UTF_8))
        } catch (e: IllegalArgumentException) {
            try {
                val bytes = Base64.getMimeDecoder().decode(key.toByteArray(StandardCharsets.UTF_8))
                if (bytes.isEmpty()) {
                    throw Exception("$key Base64.getMimeDecoder()转byte为null")
                }
                bytes
            } catch (e1: Exception) {
                throw Exception("$key Base64.getMimeDecoder()报错", e1)
            }
        }
    }

    private fun random(max: BigInteger): BigInteger {
        var r = BigInteger(256, random)
        while (r >= max) {
            r = BigInteger(128, random)
        }
        return r
    }

    private fun allZero(buffer: ByteArray?): Boolean {
        requireNotNull(buffer) { "buffer为null" }
        require(buffer.isNotEmpty()) { "buffer为空数组" }
        return !buffer.any { it.toInt() != 0 }
    }

    fun encrypt(input: String?): String? {
        return if (input.isNullOrBlank()) {
            null
        } else {
            encrypt(PUBLIC_KEY_VALUE, input)
        }
    }

    fun decrypt(input: String?): String? {
        return if (input.isNullOrBlank()) {
            null
        } else {
            decrypt(PRIVATE_KEY_VALUE, input)
        }
    }
}
