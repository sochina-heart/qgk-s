package com.sochina.demo.utils.web

import com.sochina.demo.constants.Constants

class R<T> {
    var code: Int = 0
    var msg: String? = null
    var data: T? = null

    companion object {
        /**
         * 成功
         */
        private const val SUCCESS: Int = Constants.SUCCESS

        /**
         * 失败
         */
        private const val FAIL: Int = Constants.ERROR
        fun <T> ok(): R<T?> {
            return restResult(null, SUCCESS, null)
        }

        fun <T> ok(data: T): R<T> {
            return restResult(data, SUCCESS, null)
        }

        fun <T> ok(data: T, msg: String?): R<T> {
            return restResult(data, SUCCESS, msg)
        }

        fun <T> fail(): R<T?> {
            return restResult(null, FAIL, null)
        }

        fun <T> fail(msg: String?): R<T?> {
            return restResult(null, FAIL, msg)
        }

        fun <T> fail(data: T): R<T> {
            return restResult(data, FAIL, null)
        }

        fun <T> fail(data: T, msg: String?): R<T> {
            return restResult(data, FAIL, msg)
        }

        fun <T> fail(code: Int, msg: String?): R<T?> {
            return restResult(null, code, msg)
        }

        private fun <T> restResult(data: T, code: Int, msg: String?): R<T> {
            val apiResult: R<T> = R()
            apiResult.code = code
            apiResult.data = data
            apiResult.msg = msg
            return apiResult
        }

        fun <T> isError(ret: R<T>): Boolean {
            return !isSuccess(ret)
        }

        fun <T> isSuccess(ret: R<T>): Boolean {
            return SUCCESS == ret.code
        }
    }
}