package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableName

@TableName("sochina_resource")
class Resource: BaseDomain() {

    @TableField("resource_id")
    var resourceId: String? = null

    @TableField("resource_name")
    var resourceName: String? = null

    @TableField("app_id")
    var appId: String? = null

    var path: String? = null

    var component: String? = null

    var perms: String? = null

    @TableField("is_frame")
    var isFrame: String? = null

    @TableField("is_cache")
    var isCache: String? = null

    @TableField("menu_type")
    var menuType: String? = null

    var visible: String? = null

    @TableField("order_num")
    var orderNum: Int? = null

    @TableField(exist = false)
    var children: List<Resource> = emptyList()
}