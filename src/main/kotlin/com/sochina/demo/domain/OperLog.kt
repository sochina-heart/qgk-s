package com.sochina.demo.domain

import java.io.Serializable
import java.util.*

class OperLog : Serializable {
    /**
     * 日志主键
     */
    var operId: Long? = null

    /**
     * 操作模块
     */
    var title: String? = null

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    var businessType: Int? = null

    /**
     * 业务类型数组
     */
    var businessTypes: IntArray = intArrayOf()

    /**
     * 请求方法
     */
    var method: String? = null

    /**
     * 请求方式
     */
    var requestMethod: String? = null

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    var operatorType: Int? = null

    /**
     * 请求url
     */
    var operUrl: String? = null

    /**
     * 操作地址
     */
    var operIp: String? = null

    /**
     * 请求头
     */
    var requestHeaders: String? = null

    /**
     * 请求参数
     */
    var operParam: String? = null

    /**
     * 响应头
     */
    var responseHeader: String? = null

    /**
     * 返回参数
     */
    var jsonResult: String? = null

    /**
     * 操作状态（0正常 1异常）
     */
    var status: Int? = null

    /**
     * 错误消息
     */
    var errorMsg: String? = null

    /**
     * 操作时间
     */
    var operTime: Date? = null
}