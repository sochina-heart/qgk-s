package com.sochina.demo.utils.character

class CharUtils {

    companion object {

        /**
         * ASCII表中可见字符从!开始，偏移位值为33(Decimal)
         * 半角
         */
        private const val DBC_CHAR_START: Char = 33.toChar()

        /**
         * ASCII表中可见字符到~结束，偏移位值为126(Decimal)
         * 半角
         */
        private const val DBC_CHAR_END: Char = 126.toChar()

        /**
         * 全角对应于ASCII表的可见字符从！开始，偏移值为65281
         * 全角
         */
        private const val SBC_CHAR_START: Char = 65281.toChar()

        /**
         * 全角对应于ASCII表的可见字符到～结束，偏移值为65374
         * 全角
         */
        private const val SBC_CHAR_END: Char = 65374.toChar()

        /**
         * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移
         * 全角半角转换间隔
         */
        private const val CONVERT_STEP: Int = 65248

        /**
         * 全角空格的值，它没有遵从与ASCII的相对偏移，必须特殊处理
         * 全角空格 12288
         */
        private const val SBC_SPACE: Char = 12288.toChar()

        /**
         * 半角空格的值，在ASCII中为32(Decimal)
         * 半角空格
         */
        private const val DBC_SPACE: Char = ' '

        @JvmStatic

        fun half2Full(src: String?): String? {
            if (src == null) {
                return null
            }
            val c = src.toCharArray()
            for (i in c.indices) {
                if (c[i] == DBC_SPACE) {
                    c[i] = SBC_SPACE
                } else if (c[i] in DBC_CHAR_START..DBC_CHAR_END) {
                    c[i] = (c[i].code + CONVERT_STEP).toChar()
                }
            }
            return String(c)
        }

        /**
         * 全角字符->半角字符转换
         *
         * @param src 要转换的包含全角字符的任意字符串
         * @return 半角字符串
         */
        @JvmStatic
        fun full2Half(src: String?): String? {
            if (src == null) {
                return null
            }
            val c = src.toCharArray()
            for (i in c.indices) {
                if (c[i] == SBC_SPACE) {
                    c[i] = DBC_SPACE
                } else if (c[i] in SBC_CHAR_START..SBC_CHAR_END) {
                    c[i] = (c[i].code - CONVERT_STEP).toChar()
                }
            }
            return String(c)
        }
    }
}