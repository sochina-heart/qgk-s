package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@TableName("sochina_resource")
class Resource: BaseDomain() {

    @TableId("resource_id")
    var resourceId: String? = null

    @TableField("resource_name")
    var resourceName: String? = null

    @TableField("app_id")
    var appId: String? = null

    var path: String? = null

    var component: String? = null

    var perms: String? = null

    var frame: String? = null

    var cache: String? = null

    @TableField("menu_type")
    var menuType: String? = null

    var visible: String? = null

    @TableField("order_num")
    var orderNum: Int? = null

    @TableField(exist = false)
    var children: List<Resource> = emptyList()
}