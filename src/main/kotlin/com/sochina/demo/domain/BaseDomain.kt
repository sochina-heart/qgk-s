package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.awt.image.BufferedImage
import java.util.Date

open class BaseDomain {

    var state: String? = null

    @TableField("delete_flag")
    var deleteFlag: String? = null

    @TableField("create_by")
    var createBy: String? = null

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createTime: Date? = null

    @TableField("update_by")
    var updateBy: String? = null

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var updateTime: Date? = null

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @TableField(exist = false)
    var page: Page<Any>? = null
}

open class Page<T> {

    var records: List<T>? = null

    var pageNumber: Int = 0

    var pageSize: Int = 0

    var totalPage: Long = 0

    var totalRow: Long = 0
}

class VerificationCode {
    var image: BufferedImage? = null
    var code: String? = null
}

class Ids {
    var ids: List<String> = emptyList()
}

class OperLog {
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