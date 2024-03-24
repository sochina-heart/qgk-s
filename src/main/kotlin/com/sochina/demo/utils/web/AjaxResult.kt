package com.sochina.demo.utils.web

import cn.hutool.http.HttpStatus

// Include.Include.ALWAYS 默认
// Include.NON_DEFAULT 属性为默认值不序列化
// Include.NON_EMPTY 属性为 空（""） 或者为 NULL 都不序列化
// Include.NON_NULL 属性为NULL 不序列化
// @JsonInclude(JsonInclude.Include.NON_NULL)
class AjaxResult : HashMap<String?, Any?> {
    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    constructor()

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    constructor(code: Int, msg: String?) {
        super.put(CODE_TAG, code)
        super.put(MSG_TAG, msg)
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    constructor(code: Int, msg: String?, data: Any?) {
        super.put(CODE_TAG, code)
        super.put(MSG_TAG, msg)
        if (data != null) {
            super.put(DATA_TAG, data)
        }
    }

    /**
     * 方便链式调用
     *
     * @param key   键
     * @param value 值
     * @return 数据对象
     */
    override fun put(key: String?, value: Any?): AjaxResult {
        super.put(key, value)
        return this
    }

    companion object {
        /**
         * 状态码
         */
        private const val CODE_TAG: String = "code"

        /**
         * 返回内容
         */
        private const val MSG_TAG: String = "msg"

        /**
         * 数据对象
         */
        private const val DATA_TAG: String = "data"
        private const val WARN = 601

        /**
         * 返回成功消息
         *
         * @return 成功消息
         */
        fun success(): AjaxResult {
            return success("操作成功")
        }

        /**
         * 返回成功数据
         *
         * @return 成功消息
         */
        fun success(data: Any?): AjaxResult {
            return success("操作成功", data)
        }

        /**
         * 返回成功消息
         *
         * @param msg 返回内容
         * @return 成功消息
         */
        fun success(msg: String?): AjaxResult {
            return success(msg, null)
        }

        /**
         * 返回成功消息
         *
         * @param msg  返回内容
         * @param data 数据对象
         * @return 成功消息
         */
        fun success(msg: String?, data: Any?): AjaxResult {
            return AjaxResult(HttpStatus.HTTP_OK, msg, data)
        }

        /**
         * 返回警告消息
         *
         * @param msg 返回内容
         * @return 警告消息
         */
        fun warn(msg: String?): AjaxResult {
            return warn(msg, null)
        }

        /**
         * 返回警告消息
         *
         * @param msg  返回内容
         * @param data 数据对象
         * @return 警告消息
         */
        fun warn(msg: String?, data: Any?): AjaxResult {
            return AjaxResult(WARN, msg, data)
        }

        /**
         * 返回错误消息
         *
         * @return 错误消息
         */
        fun error(): AjaxResult {
            return error("操作失败")
        }

        /**
         * 返回错误消息
         *
         * @param msg 返回内容
         * @return 错误消息
         */
        fun error(msg: String?): AjaxResult {
            return error(msg, null)
        }

        /**
         * 返回错误消息
         *
         * @param msg  返回内容
         * @param data 数据对象
         * @return 错误消息
         */
        fun error(msg: String?, data: Any?): AjaxResult {
            return AjaxResult(HttpStatus.HTTP_INTERNAL_ERROR, msg, data)
        }

        /**
         * 返回错误消息
         *
         * @param code 状态码
         * @param msg  返回内容
         * @return 错误消息
         */
        fun error(code: Int, msg: String?): AjaxResult {
            return AjaxResult(code, msg, null)
        }

        /**
         * 响应返回结果
         *
         * @param rows 影响行数
         * @return 操作结果
         */
        fun toAjax(rows: Int): AjaxResult {
            return if (rows > 0) success() else error()
        }

        /**
         * 响应返回结果
         *
         * @param result 结果
         * @return 操作结果
         */
        fun toAjax(result: Boolean): AjaxResult {
            return if (result) success() else error()
        }
    }
}
