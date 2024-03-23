package com.sochina.demo.utils.encrypt.gm.sm2

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigInteger
import java.util.*

object SM2Helper {
    private val HEX_DIGITS = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
        'F'
    )
    private const val IV_HEX_STR = "7380166f 4914b2b9 172442d7 da8a0600 a96f30bc 163138aa e38dee4d b0fb0e4e"
    private val IV = BigInteger(IV_HEX_STR.replace(" ".toRegex(), ""), 16)
    private val TJ15 = "79cc4519".toInt(16)
    private val TJ63 = "7a879d8a".toInt(16)
    private val FIRST_PADDING = byteArrayOf(0x80.toByte())
    private val ZERO_PADDING = byteArrayOf(0x00.toByte())

    private fun t(j: Int): Int {
        return when (j) {
            in 0..15 -> {
                TJ15
            }

            in 16..63 -> {
                TJ63
            }

            else -> {
                throw Exception("data invalid")
            }
        }
    }

    private fun ff(x: Int, y: Int, z: Int, j: Int): Int {
        return when (j) {
            in 0..15 -> {
                x xor y xor z
            }

            in 16..63 -> {
                x and y or (x and z) or (y and z)
            }

            else -> {
                throw Exception("data invalid")
            }
        }
    }

    private fun gg(x: Int, y: Int, z: Int, j: Int): Int {
        return when (j) {
            in 0..15 -> {
                x xor y xor z
            }

            in 16..63 -> {
                x and y or (x.inv() and z)
            }

            else -> {
                throw Exception("data invalid")
            }
        }
    }

    private fun p0(x: Int): Int {
        return x xor Integer.rotateLeft(x, 9) xor Integer.rotateLeft(x, 17)
    }

    private fun p1(x: Int): Int {
        return x xor Integer.rotateLeft(x, 15) xor Integer.rotateLeft(x, 23)
    }

    @Throws(IOException::class)
    private fun padding(source: ByteArray): ByteArray {
        val l = (source.size * 8).toLong()
        var k = 448 - (l + 1) % 512
        if (k < 0) {
            k += 512
        }
        val baos = ByteArrayOutputStream()
        baos.write(source)
        baos.write(FIRST_PADDING)
        var i = k - 7
        while (i > 0) {
            baos.write(ZERO_PADDING)
            i -= 8
        }
        baos.write(long2bytes(l))
        return baos.toByteArray()
    }

    private fun long2bytes(l: Long): ByteArray {
        val bytes = ByteArray(8)
        for (i in 0..7) {
            bytes[i] = (l ushr ((7 - i) * 8)).toByte()
        }
        return bytes
    }

    @Throws(IOException::class)
    fun hash(source: ByteArray): ByteArray? {
        val m1 = padding(source)
        val n = m1.size / (512 / 8)
        var b: ByteArray
        var vi = IV.toByteArray()
        var vi1: ByteArray? = null
        for (i in 0 until n) {
            b = Arrays.copyOfRange(m1, i * 64, (i + 1) * 64)
            vi1 = cf(vi, b)
            vi = vi1
        }
        return vi1
    }

    @Throws(IOException::class)
    private fun cf(vi: ByteArray?, bi: ByteArray): ByteArray {
        var a: Int
        var b: Int
        var c: Int
        var d: Int
        var e: Int
        var f: Int
        var g: Int
        var h: Int
        a = toInteger(vi, 0)
        b = toInteger(vi, 1)
        c = toInteger(vi, 2)
        d = toInteger(vi, 3)
        e = toInteger(vi, 4)
        f = toInteger(vi, 5)
        g = toInteger(vi, 6)
        h = toInteger(vi, 7)
        val w = IntArray(68)
        val img_width = IntArray(64)
        for (i in 0..15) {
            w[i] = toInteger(bi, i)
        }
        for (j in 16..67) {
            w[j] = (p1(w[j - 16] xor w[j - 9] xor Integer.rotateLeft(w[j - 3], 15)) xor Integer.rotateLeft(
                w[j - 13], 7
            )
                    xor w[j - 6])
        }
        for (j in 0..63) {
            img_width[j] = w[j] xor w[j + 4]
        }
        var ss1: Int
        var ss2: Int
        var tt1: Int
        var tt2: Int
        for (j in 0..63) {
            ss1 = Integer.rotateLeft(Integer.rotateLeft(a, 12) + e + Integer.rotateLeft(t(j), j), 7)
            ss2 = ss1 xor Integer.rotateLeft(a, 12)
            tt1 = ff(a, b, c, j) + d + ss2 + img_width[j]
            tt2 = gg(e, f, g, j) + h + ss1 + w[j]
            d = c
            c = Integer.rotateLeft(b, 9)
            b = a
            a = tt1
            h = g
            g = Integer.rotateLeft(f, 19)
            f = e
            e = p0(tt2)
        }
        val v = toByteArray(a, b, c, d, e, f, g, h)
        for (i in v.indices) {
            v[i] = (v[i].toInt() xor vi!![i].toInt()).toByte()
        }
        return v
    }

    private fun toInteger(source: ByteArray?, index: Int): Int {
        val valueStr = StringBuilder()
        for (i in 0..3) {
            valueStr.append(
                HEX_DIGITS[((source!![index * 4 + i].toInt() and 0xF0) shr 4).toByte()
                    .toInt()]
            )
            valueStr.append(HEX_DIGITS[(source[index * 4 + i].toInt() and 0x0F).toByte().toInt()])
        }
        return valueStr.toString().toLong(16).toInt()
    }

    @Throws(IOException::class)
    private fun toByteArray(a: Int, b: Int, c: Int, d: Int, e: Int, f: Int, g: Int, h: Int): ByteArray {
        val baos = ByteArrayOutputStream(32)
        baos.write(toByteArray(a))
        baos.write(toByteArray(b))
        baos.write(toByteArray(c))
        baos.write(toByteArray(d))
        baos.write(toByteArray(e))
        baos.write(toByteArray(f))
        baos.write(toByteArray(g))
        baos.write(toByteArray(h))
        return baos.toByteArray()
    }

    fun toByteArray(i: Int): ByteArray {
        val byteArray = ByteArray(4)
        byteArray[0] = (i ushr 24).toByte()
        byteArray[1] = ((i and 0xFFFFFF) ushr 16).toByte()
        byteArray[2] = ((i and 0xFFFF) ushr 8).toByte()
        byteArray[3] = (i and 0xFF).toByte()
        return byteArray
    }

    private fun byteToHexString(b: Byte): String {
        // return "%02x".format(b.toInt() and 0xFF)
        return (b.toInt() and 0xFF).toString(16).padStart(2, '0')
    }

    fun byteArrayToHexString(b: ByteArray): String {
        return b.joinToString("") { byteToHexString(it) }
    }
}
