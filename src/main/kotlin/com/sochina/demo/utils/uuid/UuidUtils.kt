package com.sochina.demo.utils.uuid

class UuidUtils private constructor() {
    init {
        throw IllegalStateException("UuidUtils class")
    }

    companion object {
        /**
         * 获取随机UUID
         *
         * @return 随机UUID
         */
        @JvmStatic
        fun randomUUID(): String {
            return Uuid.randomUUID().toString()
        }

        /**
         * 简化的UUID，去掉了横线
         *
         * @return 简化的UUID，去掉了横线
         */
        @JvmStatic
        fun simpleUUID(): String {
            return Uuid.randomUUID().toString(true)
        }

        /**
         * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
         *
         * @return 随机UUID
         */
        @JvmStatic
        fun fastUUID(): String {
            return Uuid.fastUUID().toString()
        }

        /**
         * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
         *
         * @return 简化的UUID，去掉了横线
         */
        @JvmStatic
        fun fastSimpleUUID(): String {
            return Uuid.fastUUID().toString(true)
        }
    }
}