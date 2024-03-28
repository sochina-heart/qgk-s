package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

@TableName("sochina_user")
class SochinaUser : BaseDomain() {

    @TableId("user_id")
    var userId: String? = null

    @Length(min = 1, max = 100, message = "账号长度必须在1-100之间")
    var account: String? = null

    @Length(min = 1, max = 100, message = "用户名长度必须在1-100之间")
    @TableField("user_name")
    var userName: String? = null

    @TableField("user_password")
    var userPassword: String? = null

    @Length(max = 1, message = "性别长度为1")
    var sex: String? = null

    @NotBlank(message = "邮箱不能为空")
    @TableField("user_email")
    var userEmail: String? = null

    @Length(max = 255, message = "地址长度不能超过255")
    @TableField("home_address")
    var homeAddress: String? = null

    @Length(max = 255, message = "个人描述长度不能超过255")
    @TableField("personal_description")
    var personalDescription: String? = null
}