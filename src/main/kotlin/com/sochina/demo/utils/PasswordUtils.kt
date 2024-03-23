package com.sochina.demo.utils

object PasswordUtils {

    /**
     * 校验密码的合法性
     *
     * @param pwd 待校验的密码
     * @return 密码是否合法，合法返回true，否则返回false
     */
    fun validate(pwd: String): Int {
        var count: Int = 0
        return with(pwd) {
            if (length > 8) count++
            listOf(
                ".*[a-z].*",
                ".*[A-Z].*",
                ".*\\d.*"
            ).count { pattern ->
                this.matches(Regex(pattern))
            }.let { matchedPatternsCount ->
                count + matchedPatternsCount
            }
        }
    }
}