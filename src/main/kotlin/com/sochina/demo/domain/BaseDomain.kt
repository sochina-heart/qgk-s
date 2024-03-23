package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.util.*

open class BaseDomain : Serializable {

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