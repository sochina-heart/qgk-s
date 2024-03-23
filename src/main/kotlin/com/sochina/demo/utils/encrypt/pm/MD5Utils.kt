package com.sochina.demo.utils.encrypt.pm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object MD5Utils {
    val HEX_DIGITS: CharArray =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val HEX_DIGITS_LOWER: CharArray =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val LOGGER: Logger = LoggerFactory.getLogger(MD5Utils::class.java)

    /**
     * 对字符串 MD5 无盐值加密
     *
     * @param plainText 传入要加密的字符串
     * @return MD5加密后生成32位(小写字母 + 数字)字符串
     */
    fun MD5Lower(plainText: String): String? {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            val md = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            md.update(plainText.toByteArray())
            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值。1 固定值
            return BigInteger(1, md.digest()).toString(16)
        } catch (e: NoSuchAlgorithmException) {
            LOGGER.error(e.message)
            return null
        }
    }

    /**
     * 对字符串 MD5 加密
     *
     * @param plainText 传入要加密的字符串
     * @return MD5加密后生成32位(大写字母 + 数字)字符串
     */
    fun MD5Upper(plainText: String): String? {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            val md = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            md.update(plainText.toByteArray())
            // 获得密文
            val mdResult = md.digest()
            // 把密文转换成十六进制的字符串形式
            val j = mdResult.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val byte0 = mdResult[i]
                // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移
                str[k++] = HEX_DIGITS[byte0.toInt() ushr 4 and 0xf]
                // 取字节中低 4 位的数字转换
                str[k++] = HEX_DIGITS[byte0.toInt() and 0xf]
            }
            return String(str)
        } catch (e: Exception) {
            LOGGER.error(e.message)
            return null
        }
    }

    /**
     * 对字符串 MD5 加盐值加密
     *
     * @param plainText 传入要加密的字符串
     * @param saltValue 传入要加的盐值
     * @return MD5加密后生成32位(小写字母 + 数字)字符串
     */
    fun MD5Lower(saltValue: String, plainText: String): String? {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            val md = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            md.update(plainText.toByteArray())
            md.update(saltValue.toByteArray())
            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值。1 固定值
            return BigInteger(1, md.digest()).toString(16)
        } catch (e: NoSuchAlgorithmException) {
            LOGGER.error(e.message)
            return null
        }
    }

    /**
     * 对字符串 MD5 加盐值加密
     *
     * @param plainText 传入要加密的字符串
     * @param saltValue 传入要加的盐值
     * @return MD5加密后生成32位(大写字母 + 数字)字符串
     */
    fun MD5Upper(saltValue: String, plainText: String): String? {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            val md = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            md.update(plainText.toByteArray())
            md.update(saltValue.toByteArray())
            // 获得密文
            val mdResult = md.digest()
            // 把密文转换成十六进制的字符串形式
            val j = mdResult.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val byte0 = mdResult[i]
                str[k++] = HEX_DIGITS[byte0.toInt() ushr 4 and 0xf]
                str[k++] = HEX_DIGITS[byte0.toInt() and 0xf]
            }
            return String(str)
        } catch (e: Exception) {
            LOGGER.error(e.message)
            return null
        }
    }

    /**
     * MD5加密后生成32位(小写字母+数字)字符串
     * 同 MD5Lower() 一样
     */
    fun MD5(plainText: String): String? {
        try {
            val mdTemp = MessageDigest.getInstance("MD5")
            mdTemp.update(plainText.toByteArray(charset("UTF-8")))
            val md = mdTemp.digest()
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
            for (i in 0 until j) {
                val byte0 = md[i]
                str[k++] = HEX_DIGITS_LOWER[byte0.toInt() ushr 4 and 0xf]
                str[k++] = HEX_DIGITS_LOWER[byte0.toInt() and 0xf]
            }
            return String(str)
        } catch (e: Exception) {
            LOGGER.error(e.message)
            return null
        }
    }
}
