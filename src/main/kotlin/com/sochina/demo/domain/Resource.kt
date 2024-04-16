package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@TableName("sochina_resource")
class Resource: BaseDomain() {

    @TableId("resource_id")
    var resourceId: String = ""

    @TableField("resource_name")
    var resourceName: String = ""

    @TableField("app_id")
    var appId: String = ""

    @TableField("parent_id")
    var parentId: String = ""

    var path: String = ""

    var component: String = ""

    var perms: String = ""

    @TableField("ancestor_list")
    var ancestorList = ""

    var frame: String = ""

    var cache: String = ""

    @TableField("menu_type")
    var menuType: String = ""

    var visible: String? = ""

    @TableField("order_num")
    var orderNum: Int = 1
}

class ResourceVo {
    var id: String = ""

    var label: String = ""

    var type: String = ""

    var children: List<ResourceVo> = emptyList()
}