package com.sochina.demo.constants

import java.util.regex.Pattern

object Constants {
    /**
     * 通用成功标识
     */
    const val SUCCESS: String = "0"

    /**
     * 通用失败标识
     */
    const val FAIL: String = "1"

    /**
     * UTF-8 字符集
     */
    const val UTF8: String = "UTF-8"

    /**
     * GBK 字符集
     */
    const val GBK: String = "GBK"

    /**
     * www主域
     */
    const val WWW: String = "www."

    /**
     * http请求
     */
    const val HTTP: String = "http://"

    /**
     * https请求
     */
    const val HTTPS: String = "https://"
    const val IP_LOCAL: String = "127.0.0.1"
    const val IPV6_LOCAL: String = "0:0:0:0:0:0:0:1"

    /**
     * 资源映射路径 前缀
     */
    const val RESOURCE_PREFIX: String = "/profile"

    /**
     * bootstrap.yml
     */
    const val BOOTSTRAP_YML: String = "bootstrap.yml"

    /**
     * jasypt加解密盐值
     */
    const val JASYPT_ENCRYPTOR_PASSWORD: String = "jasypt.encryptor.password"

    /**
     * 当前记录起始索引
     */
    const val PAGE_NUM: String = "pageNum"

    /**
     * 每页显示记录数
     */
    const val PAGE_SIZE: String = "pageSize"

    /**
     * 英文逗号
     */
    const val COMMA: String = ","
    const val ADD: String = "+"
    const val REDUCE: String = "-"
    const val TAKE: String = "*"
    const val REMOVE: String = "/"
    const val COLON: String = ":"

    /**
     * 空字符串
     */
    const val EMPTY_STRING: String = ""

    /**
     * 半角句号
     */
    const val HALF_STOP: String = "."

    /**
     * 半角问号
     */
    const val HALF_ASK_MARK: String = "?"

    /**
     * 半角括号
     */
    const val HALF_BRACKET: String = "()"

    /**
     * null
     */
    val EMPTY_NULL: String? = null

    /**
     * Pattern空数组
     */
    val EMPTY_PATTERN_ARRAY: Array<Pattern?> = arrayOfNulls(0)

    /**
     * String空数组
     */
    val EMPTY_STRING_ARRAY: Array<String?> = arrayOfNulls(0)

    /**
     * String空List
     */
    val EMPTY_LIST: List<String> = ArrayList(0)

    /**
     * Map<String></String>, String> 空Map
     */
    val EMPTY_MAP: Map<String, String> = emptyMap()

    /**
     * 下划线 char
     */
    const val UNDERLINE_CHAR: Char = '_'

    /**
     * 下划线 String
     */
    const val UNDERLINE_STRING: String = "_"

    /**
     * 等于号
     */
    const val EQUAL_SIGN: String = "="

    /**
     * 并且
     */
    const val AND_SIGN: String = "&"
}