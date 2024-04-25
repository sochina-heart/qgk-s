package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.bouncycastle.util.encoders.UTF8
import org.hibernate.validator.constraints.Length

@TableName("sochina_user")
class SochinaUser : BaseDomain() {

    @TableField(exist = false)
    var appId: String = ""

    @TableId("user_id")
    var userId: String = ""

    @Length(min = 1, max = 100, message = "账号长度必须在1-100之间")
    var account: String = ""

    @Length(min = 1, max = 100, message = "用户名长度必须在1-100之间")
    @TableField("user_name")
    var userName: String = ""

    @TableField("user_password")
    var userPassword: String = ""

    @Length(max = 1, message = "性别长度为1")
    var sex: String = ""

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @TableField("user_email")
    var userEmail: String = ""

    @Length(max = 255, message = "地址长度不能超过255")
    @TableField("home_address")
    var homeAddress: String = ""

    @Length(max = 255, message = "个人描述长度不能超过255")
    @TableField("personal_description")
    var personalDescription: String = ""
}