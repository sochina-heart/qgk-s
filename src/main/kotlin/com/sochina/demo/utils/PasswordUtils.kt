package com.sochina.demo.utils

object PasswordUtils {

    /**
     * 校验密码的合法性
     *
     * @param pwd 待校验的密码
     * @return count
     */
    fun validate(pwd: String): Int {
        var count: Int = 0
        return with(pwd) {
            if (length > 8) count++
            listOf(
                ".*[a-z].*",
                ".*[A-Z].*",
                ".*\\d.*",
                ".*\\p{Punct}.*"
            ).count { pattern ->
                this.matches(Regex(pattern))
            }.let { matchedPatternsCount ->
                count + matchedPatternsCount
            }
        }
    }
}