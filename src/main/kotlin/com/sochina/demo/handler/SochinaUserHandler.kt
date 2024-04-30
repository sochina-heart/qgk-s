package com.sochina.demo.handler

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sochina.demo.constants.ErrorEnum
import com.sochina.demo.domain.Ids
import com.sochina.demo.domain.MenuItem
import com.sochina.demo.domain.ModifyState
import com.sochina.demo.domain.SochinaUser
import com.sochina.demo.mapper.ResourceMapper
import com.sochina.demo.mapper.SochinaUserMapper
import com.sochina.demo.utils.PasswordUtils
import com.sochina.demo.utils.encrypt.gm.sm4.SM4Utils
import com.sochina.demo.utils.uuid.UuidUtils
import com.sochina.demo.utils.web.AjaxResult
import io.quarkus.cache.CacheKey
import io.quarkus.cache.CacheResult
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import jakarta.validation.Valid
import jakarta.ws.rs.GET
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import java.util.*
import java.util.logging.Logger


@Path("/user")
class SochinaUserHandler(
    private val baseMapper: SochinaUserMapper,
    private val resourceMapper: ResourceMapper,
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
            user.userPassword = ""
            user.salt = ""
            AjaxResult.success(user)
        }
    }

    @POST
    @Path("/list")
    fun listUser(sochinaUser: SochinaUser): Uni<AjaxResult> {
        val queryWrapper = QueryWrapper<SochinaUser>()
            .eq("delete_flag", "0")
            .apply {
                sochinaUser.account.takeIf { it.isNotBlank() }?.let { like("account", it) }
                sochinaUser.userName.takeIf { it.isNotBlank() }?.let { like("user_name", it) }
                sochinaUser.userEmail.takeIf { it.isNotBlank() }?.let { like("user_email", it) }
                sochinaUser.homeAddress.takeIf { it.isNotBlank() }?.let { like("home_address", it) }
                sochinaUser.sex.takeIf { it.isNotBlank() }?.let { eq("sex", it) }
            }
            .orderByDesc("update_time")
            .select("user_id", "account", "user_name", "sex", "user_email", "state")
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
                sochinaUser.userPassword.isEmpty() -> AjaxResult.error("user password is empty")
                (PasswordUtils.validate(sochinaUser.userPassword) < 4) -> AjaxResult.error("user password is weak password")
                (baseMapper.isExist(
                    sochinaUser.userId,
                    sochinaUser.account
                ) > 0) -> AjaxResult.error("user has already")

                else -> {
                    sochinaUser.createTime = Date()
                    sochinaUser.userId = UuidUtils.fastSimpleUUID()
                    sochinaUser.deleteFlag = "0"
                    sochinaUser.salt = UuidUtils.fastSimpleUUID()
                    sochinaUser.userPassword = SM4Utils.encryptPass(sochinaUser.userPassword, sochinaUser.salt)
                    AjaxResult.toAjax(baseMapper.insert(sochinaUser))
                }
            }
        }
    }

    fun updateUser(sochinaUser: SochinaUser): Uni<AjaxResult> {
        return uni {
            if (baseMapper.isExist(sochinaUser.userId, sochinaUser.account) > 0) {
                AjaxResult.success("user has already")
            } else {
                AjaxResult.toAjax(baseMapper.updateById(sochinaUser))
            }
        }
    }

    @POST
    @Path("/save")
    fun saveUser(@Valid sochinaUser: SochinaUser): Uni<AjaxResult> {
        return if (sochinaUser.userId.isEmpty()) {
            addUser(sochinaUser)
        } else {
            updateUser(sochinaUser)
        }
    }

    @POST
    @Path("/remove")
    fun removeUser(ids: Ids): Uni<AjaxResult> {
        return uni {
            if (ids.ids.isEmpty()) {
                AjaxResult.success()
            } else {
                AjaxResult.toAjax(baseMapper.removeBatchById(ids.ids))
            }
        }
    }

    @POST
    @Path("/changeState")
    fun changeState(modifyState: ModifyState): Uni<AjaxResult> {
        return uni { AjaxResult.toAjax(baseMapper.changeState(modifyState.id, modifyState.state)) }
    }

    @POST
    @Path("/login")
    fun login(sochinaUser: SochinaUser): AjaxResult {
        sochinaUser.userPassword = SM4Utils.desEncrypt(sochinaUser.userPassword)
        if (sochinaUser.account.isEmpty() || sochinaUser.userPassword.isEmpty()) {
            return AjaxResult.error(ErrorEnum.ERROR_USER_INFO.code, ErrorEnum.ERROR_USER_INFO.message)
        }
        val queryWrapper = QueryWrapper<SochinaUser>().eq("account", sochinaUser.account)
            .eq("state", "0")
            .eq("delete_flag", "0")
            .select("user_id", "user_password", "salt")
        val user = baseMapper.selectOne(queryWrapper)
            ?: return AjaxResult.error(ErrorEnum.ERROR_USER_INFO.code, ErrorEnum.ERROR_USER_INFO.message);
        if (!SM4Utils.checkPassword(sochinaUser.userPassword, user.salt, user.userPassword)) {
            return AjaxResult.error(ErrorEnum.ERROR_USER_INFO.code, ErrorEnum.ERROR_USER_INFO.message);
        }
        val token = SM4Utils.encryptCbc(user.userId + "-" + UuidUtils.fastSimpleUUID())
        cachePerms(token!!, user.userId, sochinaUser.appId)
        return AjaxResult.success(mapOf("tn" to token));
    }

    @CacheResult(cacheName = "sochinaPerms")
    fun cachePerms(@CacheKey token: String, userId: String, appId: String): List<String> = baseMapper.getPermsByUserId(userId, appId)

    @GET
    @Path("/getRouter")
    fun getRouter(
        @QueryParam("appId") appId: String,
        @HeaderParam("Authorization") token: String
    ): AjaxResult {
        return AjaxResult.success(cacheRouter(token, appId))
    }

    @CacheResult(cacheName = "sochinaRouter")
    fun cacheRouter(@CacheKey token: String, appId: String): List<MenuItem> {
        val permsList = cachePerms(token, SM4Utils.decryptCbc(token)!!.split("-")[0], appId)
        val list = resourceMapper.getRouter(appId).filter { it.path in permsList }
        return getRouterTree(list, "0")
    }

    private fun getRouterTree(list: List<MenuItem>, parentId: String): List<MenuItem> {
        val result = mutableListOf<MenuItem>()
        list.forEach {
            if (it.pid == parentId) {
                it.children = getRouterTree(list, it.id)
                result.add(it)
            }
        }
        return result
    }
}