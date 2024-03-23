package com.sochina.demo.utils

import java.nio.charset.StandardCharsets

object StringToHexUtils {
    /**
     * 字符串转换为16进制字符串
     *
     * @param s
     * @return
     */
    fun stringToHexString(s: String): String {
        return s.map { it.code.toString().padStart(2, '0') }.joinToString("")
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    fun hexStringToString(hexs: String?): String? {
        var s = hexs
        if (s == null || "" == s) {
            return null
        }
        s = s.replace(" ", "")
        val baKeyword = ByteArray(s.length / 2)
        for (i in baKeyword.indices) {
            baKeyword[i] = (0xff and s.substring(i * 2, i * 2 + 2).toInt(16)).toByte()
        }
        return String(baKeyword, StandardCharsets.UTF_8)
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    fun hexStringToByteArray(s: String): ByteArray {
        // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
        return s.chunked(2).mapNotNull { it.toIntOrNull(16)?.toByte() }.toByteArray()
    }

    /**
     * byte数组转16进制字符串
     *
     * @param bArray
     * @return
     */
    fun bytesToHexString(bArray: ByteArray): String {
        return bArray.map { byte ->
            val hex = byte.toInt() and 0XFF
            "%02x".format(hex)
        }.joinToString { "" }
    }
}