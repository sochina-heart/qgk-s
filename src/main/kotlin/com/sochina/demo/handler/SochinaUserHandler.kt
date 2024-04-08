package com.sochina.demo.handler

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sochina.demo.domain.Ids
import com.sochina.demo.domain.SochinaUser
import com.sochina.demo.mapper.SochinaUserMapper
import com.sochina.demo.utils.PasswordUtils
import com.sochina.demo.utils.encrypt.gm.SM3Utils
import com.sochina.demo.utils.uuid.UuidUtils
import com.sochina.demo.utils.web.AjaxResult
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import java.util.Date
import java.util.logging.Logger


@Path("/user")
class SochinaUserHandler(
    private val baseMapper: SochinaUserMapper,
) {

    private val logger: Logger = Logger.getLogger(SochinaUserHandler::class.java.name)

    @GET
    @Path("/get/{id}")
    fun getUser(@PathParam("id") id: String): Uni<AjaxResult> {
        return uni {
            if (id.isBlank()) {
                logger.info("user id is $id")
                AjaxResult.error("user id is empty")
            }
            val user = baseMapper.selectById(id)
            user.userPassword = null
            AjaxResult.success(user)
        }
    }

    @POST
    @Path("/list")
    fun listUser(sochinaUser: SochinaUser): Uni<AjaxResult> {
        val queryWrapper = QueryWrapper<SochinaUser>()
            .eq("delete_flag", "0")
            .apply {
                sochinaUser.account.takeIf { !it.isNullOrBlank() }?.let { like("account", it) }
                sochinaUser.userName.takeIf { !it.isNullOrBlank() }?.let { like("user_name", it) }
                sochinaUser.userEmail.takeIf { !it.isNullOrBlank() }?.let { like("user_email", it) }
                sochinaUser.homeAddress.takeIf { !it.isNullOrBlank() }?.let { like("home_address", it) }
                sochinaUser.sex.takeIf { !it.isNullOrBlank() }?.let { eq("sex", it) }
            }
            .orderByDesc("update_time")
            .select("user_id", "account", "user_name", "sex", "user_email", "home_address", "personal_description")
        val list =
            baseMapper.selectPage(
                sochinaUser.page?.let { Page(it.pageNumber.toLong(), it.pageSize.toLong()) },
                queryWrapper
            )
        return uni { AjaxResult.success(list) }
    }

    fun addUser(sochinaUser: SochinaUser): Uni<AjaxResult> {
        return uni {
            when {
                sochinaUser.userPassword.isNullOrEmpty() -> AjaxResult.error("user password is empty")
                (PasswordUtils.validate(sochinaUser.userPassword!!) < 4) -> AjaxResult.error("user password is weak password")
                (baseMapper.isExist(sochinaUser) > 0) -> AjaxResult.error("user has already")
                else -> {
                    sochinaUser.createTime = Date()
                    sochinaUser.userId = UuidUtils.fastSimpleUUID()
                    sochinaUser.deleteFlag = "0"
                    sochinaUser.userPassword = SM3Utils.encrypt(sochinaUser.userPassword!!)
                    AjaxResult.toAjax(baseMapper.insert(sochinaUser))
                }
            }
        }
    }

    fun updateUser(sochinaUser: SochinaUser): Uni<AjaxResult> {
        return uni {
            if (baseMapper.isExist(sochinaUser) > 0) {
                AjaxResult.success("user has already")
            } else {
                AjaxResult.toAjax(baseMapper.updateById(sochinaUser))
            }
        }
    }

    @POST
    @Path("/save")
    fun saveUser(@Valid sochinaUser: SochinaUser): Uni<AjaxResult> {
        return if (sochinaUser.userId.isNullOrEmpty()) {
            addUser(sochinaUser)
        } else {
            updateUser(sochinaUser)
        }
    }

    @POST
    @Path("/remove")
    @Transactional
    fun removeUser(ids: Ids): Uni<AjaxResult> {
        return uni {
            if (ids.ids.isEmpty()) {
                AjaxResult.success()
            } else {
                ids.ids.forEach {
                    baseMapper.update(
                        UpdateWrapper<SochinaUser>().set("delete_flag", "1").eq("user_id", it)
                    )
                }
            }
            AjaxResult.success()
        }
    }
}