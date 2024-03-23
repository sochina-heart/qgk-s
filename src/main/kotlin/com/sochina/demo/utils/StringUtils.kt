package com.sochina.demo.utils

import cn.hutool.core.text.StrFormatter
import java.util.*

class StringUtils {
    companion object {
        /**
         * 获取参数不为空值
         *
         * @param value defaultValue 要判断的value
         * @return value 返回值
         */
        @JvmStatic
        fun <T> nvl(value: T?, defaultValue: T): T {
            return value ?: defaultValue
        }

        /**
         * 判断一个Collection是否为空， 包含List，Set，Queue
         *
         * @param coll 要判断的Collection
         * @return true：为空 false：非空
         */
        @JvmStatic
        fun isEmpty(coll: Collection<*>): Boolean {
            return isNull(coll) || coll.isEmpty()
        }

        /**
         * 判断一个Collection是否非空，包含List，Set，Queue
         *
         * @param coll 要判断的Collection
         * @return true：非空 false：空
         */
        @JvmStatic
        fun isNotEmpty(coll: Collection<*>): Boolean {
            return !isEmpty(coll)
        }

        /**
         * 判断一个对象数组是否为空
         *
         * @param objects 要判断的对象数组
         * * @return true：为空 false：非空
         */
        @JvmStatic
        fun isEmpty(objects: Array<Any?>): Boolean {
            return isNull(objects) || (objects.isEmpty())
        }

        /**
         * 判断一个对象数组是否非空
         *
         * @param objects 要判断的对象数组
         * @return true：非空 false：空
         */
        @JvmStatic
        fun isNotEmpty(objects: Array<Any?>): Boolean {
            return !isEmpty(objects)
        }

        /**
         * 判断一个Map是否为空
         *
         * @param map 要判断的Map
         * @return true：为空 false：非空
         */
        @JvmStatic
        fun isEmpty(map: Map<*, *>): Boolean {
            return isNull(map) || map.isEmpty()
        }

        /**
         * 判断一个Map是否为空
         *
         * @param map 要判断的Map
         * @return true：非空 false：空
         */
        @JvmStatic
        fun isNotEmpty(map: Map<*, *>): Boolean {
            return !isEmpty(map)
        }

        /**
         * 判断一个字符串是否为空串
         *
         * @param str String
         * @return true：为空 false：非空
         */
        @JvmStatic
        fun isEmpty(str: String?): Boolean {
            return isNull(str) || "" == trim(
                str
            )
        }

        /**
         * 判断一个字符串是否为非空串
         *
         * @param str String
         * @return true：非空串 false：空串
         */
        @JvmStatic
        fun isNotEmpty(str: String): Boolean {
            return !isEmpty(str)
        }

        /**
         * 判断一个对象是否为空
         *
         * @param object Object
         * @return true：为空 false：非空
         */
        @JvmStatic
        fun isNull(`object`: Any?): Boolean {
            return `object` == null
        }

        /**
         * 判断一个对象是否非空
         *
         * @param object Object
         * @return true：非空 false：空
         */
        @JvmStatic
        fun isNotNull(`object`: Any?): Boolean {
            return !isNull(`object`)
        }

        /**
         * 判断一个对象是否是数组类型（Java基本型别的数组）
         *
         * @param object 对象
         * @return true：是数组 false：不是数组
         */
        @JvmStatic
        fun isArray(`object`: Any): Boolean {
            return isNotNull(`object`) && `object`.javaClass.isArray
        }

        /**
         * 去空格
         */
        fun trim(str: String?): String {
            return (str?.trim { it <= ' ' } ?: "")
        }

        /**
         * 截取字符串
         *
         * @param str   字符串
         * @param start 开始
         * @return 结果
         */
        fun substring(str: String?, start: Int): String {
            var start = start
            if (str == null) {
                return ""
            }
            if (start < 0) {
                start += str.length
            }
            if (start < 0) {
                start = 0
            }
            if (start > str.length) {
                return ""
            }
            return str.substring(start)
        }

        /**
         * 截取字符串
         *
         * @param str   字符串
         * @param start 开始
         * @param end   结束
         * @return 结果
         */
        fun substring(str: String?, start: Int, end: Int): String {
            var start = start
            var end = end
            if (str == null) {
                return ""
            }
            if (end < 0) {
                end += str.length
            }
            if (start < 0) {
                start += str.length
            }
            if (end > str.length) {
                end = str.length
            }
            if (start > end) {
                return ""
            }
            if (start < 0) {
                start = 0
            }
            if (end < 0) {
                end = 0
            }
            return str.substring(start, end)
        }

        /**
         * 格式化文本, {} 表示占位符<br></br>
         * 此方法只是简单将占位符 {} 按照顺序替换为参数<br></br>
         * 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br></br>
         * 例：<br></br>
         * 通常使用：format("this is {} for {}", "a", "b") -> this is a for b<br></br>
         * 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br></br>
         * 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br></br>
         *
         * @param template 文本模板，被替换的部分用 {} 表示
         * @param params   参数值
         * @return 格式化后的文本
         */
        @JvmStatic
        fun format(template: String, vararg params: Any?): String {
            if (params.isEmpty() || isEmpty(template)) {
                return template
            }
            return StrFormatter.format(template, *params)
        }

        /**
         * 是否为http(s)://开头
         *
         * @param link 链接
         * @return 结果
         */
        @JvmStatic
        fun isHttp(link: String?): Boolean {
            return isStartsWithStr(link, listOf("http://", "https://"))
        }

        /**
         * 字符串转set
         *
         * @param str 字符串
         * @param sep 分隔符
         * @return set集合
         */
        @JvmStatic
        fun str2Set(str: String, sep: String): Set<String> {
            return HashSet(str2List(str, sep, true, false))
        }

        /**
         * 字符串转list
         *
         * @param str         字符串
         * @param sep         分隔符
         * @param filterBlank 过滤纯空白
         * @param trim        去掉首尾空白
         * @return list集合
         */
        @JvmStatic
        fun str2List(str: String, sep: String, filterBlank: Boolean, trim: Boolean): List<String> {
            return str.split(sep).filterNot { filterBlank && it.isBlank() }.map { if (trim) it.trim() else it }
        }

        /**
         * 判断给定的set列表中是否包含数组array 判断给定的数组array中是否包含给定的元素value
         *
         * @param collection 给定的集合
         * @param array      给定的数组
         * @return boolean 结果
         */
        @JvmStatic
        fun containsAny(collection: Collection<String?>, vararg array: String?): Boolean {
            return collection.any { it in array }
        }

        /**
         * 查找指定字符串是否包含指定字符串列表中的任意一个字符串同时串忽略大小写
         *
         * @param cs                  指定字符串
         * @param searchCharSequences 需要检查的字符串数组
         * @return 是否包含任意一个字符串
         */
        fun containsAnyIgnoreCase(cs: CharSequence?, vararg searchCharSequences: CharSequence?): Boolean {
            if (cs.isNullOrEmpty() || searchCharSequences.isEmpty()) {
                return false
            }
            for (testStr in searchCharSequences) {
                if (cs.contains(testStr!!, true)) {
                    return true
                }
            }
            return false
        }

        /**
         * 驼峰转下划线命名
         */
        @JvmStatic
        fun toUnderScoreCase(str: String?): String? {
            if (str == null) {
                return null
            }
            val sb = StringBuilder()
            // 前置字符是否大写
            var preCharIsUpperCase = true
            // 当前字符是否大写
            var curreCharIsUpperCase = true
            // 下一字符是否大写
            var nexteCharIsUpperCase = true
            for (i in str.indices) {
                val c = str[i]
                preCharIsUpperCase = if (i > 0) {
                    Character.isUpperCase(str[i - 1])
                } else {
                    false
                }
                curreCharIsUpperCase = Character.isUpperCase(c)
                if (i < (str.length - 1)) {
                    nexteCharIsUpperCase = Character.isUpperCase(str[i + 1])
                }
                if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                    sb.append("_")
                } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                    sb.append("_")
                }
                sb.append(c.lowercaseChar())
            }
            return sb.toString()
        }

        /**
         * 是否包含字符串
         *
         * @param str  验证字符串
         * @param strs 字符串组
         * @return 包含返回true
         */
        @JvmStatic
        fun inStringIgnoreCase(str: String?, vararg strs: String?): Boolean {
            if (str != null) {
                for (s in strs) {
                    if (str.equals(trim(s), ignoreCase = true)) {
                        return true
                    }
                }
            }
            return false
        }

        /**
         * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
         *
         * @param name 转换前的下划线大写方式命名的字符串
         * @return 转换后的驼峰式命名的字符串
         */
        @JvmStatic
        fun convertToCamelCase(name: String?): String {
            val result = StringBuilder()
            // 快速检查
            if (name.isNullOrEmpty()) {
                // 没必要转换
                return ""
            } else if (!name.contains("_")) {
                // 不含下划线，仅将首字母大写
                return name.substring(0, 1).uppercase(Locale.getDefault()) + name.substring(1)
            }
            // 用下划线将原始字符串分割
            val camels: Array<String> = name.split("_".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (camel in camels) {
                // 跳过原始字符串中开头、结尾的下换线或双重下划线
                if (camel.isEmpty()) {
                    continue
                }
                // 首字母大写
                result.append(camel.substring(0, 1).uppercase(Locale.getDefault()))
                result.append(camel.substring(1).lowercase(Locale.getDefault()))
            }
            return result.toString()
        }

        /**
         * 驼峰式命名法
         * 例如：user_name->userName
         */
        @JvmStatic
        fun toCamelCase(s: String?): String? {
            var s = s ?: return null
            if (s.indexOf("_") == -1) {
                return s
            }
            s = s.lowercase(Locale.getDefault())
            val sb = StringBuilder(s.length)
            var upperCase = false
            for (element in s) {
                if (element == '_') {
                    upperCase = true
                } else if (upperCase) {
                    sb.append(element.uppercaseChar())
                    upperCase = false
                } else {
                    sb.append(element)
                }
            }
            return sb.toString()
        }

        /**
         * 查找指定字符串是否匹配指定字符串列表中的任意一个字符串
         *
         * @param str  指定字符串
         * @param strs 需要检查的字符串数组
         * @return 是否匹配
         */
        @JvmStatic
        fun matches(str: String, strs: List<String?>): Boolean = strs.any { this.isMatch(it!!, str) }

        /**
         * 判断url是否与规则配置:
         * ? 表示单个字符;
         * * 表示一层路径内的任意字符串，不可跨层级;
         * ** 表示任意层路径;
         *
         * @param pattern 匹配规则
         * @param url     需要匹配的url
         * @return
         */
        @JvmStatic
        fun isMatch(pattern: String, url: String): Boolean = url.matches(Regex(pattern))

        @JvmStatic
        @Suppress("UNCHECKED_CAST")
        fun <T> cast(obj: Any): T = obj as T

        /**
         * 数字左边补齐0，使之达到指定长度。注意，如果数字转换为字符串后，长度大于size，则只保留 最后size个字符。
         *
         * @param num  数字对象
         * @param size 字符串指定长度
         * @return 返回数字的字符串格式，该字符串为指定长度。
         */
        @JvmStatic
        fun padl(num: Number, size: Int): String = padl(num.toString(), size, '0')

        /**
         * 字符串左补齐。如果原始字符串s长度大于size，则只保留最后size个字符。
         *
         * @param s    原始字符串
         * @param size 字符串指定长度
         * @param c    用于补齐的字符
         * @return 返回指定长度的字符串，由原字符串左补齐或截取得到。
         */
        @JvmStatic
        fun padl(s: String?, size: Int, c: Char): String {
            return if (s != null) {
                if (s.length < size) {
                    buildString {
                        repeat(size - s.length) { append(c) }
                        append(s)
                    }
                } else {
                    s.substring(s.length - size, s.length)
                }
            } else {
                buildString {
                    repeat(size) { append(c) }
                }
            }
        }

        /**
         * 判断字符串是否与规则匹配 指定字符串结尾
         *
         * @param str  指定字符串
         * @param strs 需要匹配的字符串数组
         * @return 是否匹配
         */
        @JvmStatic
        fun isEndsWithStr(str: String, strs: List<String?>): Boolean {
            if (isEmpty(strs)) {
                return false
            }
            for (ext in strs) {
                if (str.endsWith(ext!!)) {
                    return true
                }
            }
            return false
        }

        /**
         * 判断字符串是否与规则匹配 指定字符串结尾 忽略大小写
         *
         * @param str  指定字符串
         * @param strs 需要匹配的字符串数组
         * @return 是否匹配
         */
        @JvmStatic
        fun isEndsWithStrIgnoreCase(str: String, strs: List<String?>): Boolean {
            if (isEmpty(strs)) {
                return false
            }
            for (ext in strs) {
                if (str.endsWith(ext!!, true)) {
                    return true
                }
            }
            return false
        }

        /**
         * 判断字符串是否与规则匹配 指定字符串开始
         *
         * @param str  指定字符串
         * @param strs 需要匹配的字符串数组
         * @return 是否匹配
         */
        @JvmStatic
        fun isStartsWithStr(str: String?, strs: List<String?>): Boolean {
            if (str.isNullOrEmpty() || isEmpty(
                    strs
                )
            ) {
                return false
            }
            for (ext in strs) {
                if (str.startsWith(ext!!)) {
                    return true
                }
            }
            return false
        }

        /**
         * 判断字符串是否与规则匹配 指定字符串开始 忽略大小写
         *
         * @param str  指定字符串
         * @param strs 需要匹配的字符串数组
         * @return 是否匹配
         */
        @JvmStatic
        fun isStartsWithStrIgnoreCase(str: String?, strs: List<String?>): Boolean {
            if (str.isNullOrEmpty() || isEmpty(
                    strs
                )
            ) {
                return false
            }
            for (ext in strs) {
                if (str.startsWith(ext!!, true)) {
                    return true
                }
            }
            return false
        }
    }
}