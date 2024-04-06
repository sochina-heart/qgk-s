package com.sochina.demo.domain

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

@TableName("sochina_app")
class Application: BaseDomain() {

    @TableId("app_id")
    var appId: String? = null

    @Length(min = 1, max = 100, message = "账号长度必须在1-100之间")
    @TableField("app_name")
    var appName: String? = null

    @Length(max = 1, message = "用户名长度为1")
    @TableField("app_user")
    var appUser: String? = null

    @NotBlank(message = "手机号不能为空")
    @TableField("app_phone")
    var appPhone: String? = null

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @TableField("app_email")
    var appEmail: String? = null
}