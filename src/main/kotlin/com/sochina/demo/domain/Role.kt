package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import jakarta.validation.constraints.NotBlank

@TableName("sochina_role")
class Role: BaseDomain() {

    @TableId("role_id")
    var roleId: String = ""

    @TableField("role_name")
    var roleName: String = ""

    @NotBlank(message = "唯一标识不能为空")
    var perms: String = ""

    @TableField("app_id")
    var appId: String = ""

    @TableField(exist = false)
    var resourceIds: List<String> = emptyList()
}

@TableName("sochina_role_rela_resource")
class RoleRelaResource {

    var id: String = ""

    @TableField("role_id")
    var roleId: String = ""

    @TableField("resource_id")
    var resourceId: String = ""
}