package com.sochina.demo.handler

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sochina.demo.domain.Ids
import com.sochina.demo.domain.ModifyState
import com.sochina.demo.domain.Role
import com.sochina.demo.domain.RoleRelaResource
import com.sochina.demo.mapper.RoleMapper
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
import jakarta.ws.rs.QueryParam
import java.util.*
import java.util.logging.Logger

@Path("/role")
class RoleHandler(
    private val baseMapper: RoleMapper,
) {

    private val logger: Logger = Logger.getLogger(RoleHandler::class.java.name)

    @GET
    @Path("/list")
    fun listRole(
        @QueryParam("appId") appId: String,
        @QueryParam("roleName") roleName: String,
        @QueryParam("pageNumber") pageNumber: Long,
        @QueryParam("pageSize") pageSize: Long
    ): Uni<AjaxResult> {
        return uni {
            val queryWrapper = QueryWrapper<Role>().eq("delete_flag", "0")
                .apply {
                    appId.takeIf { it.isNotBlank() }?.let { eq("app_id", it) }
                    roleName.takeIf { it.isNotBlank() }?.let { like("role_name", it) }
                }
                .select("role_id", "role_name", "perms", "state")
                .orderByDesc("update_time")
            AjaxResult.success(baseMapper.selectPage(Page(pageNumber, pageSize), queryWrapper))
        }
    }

    @GET
    @Path("/get/{id}")
    fun getRole(@PathParam("id") id: String): Uni<AjaxResult> {
        return uni {
            if (id.isBlank()) {
                logger.info("role id is $id")
                AjaxResult.error("role id is empty")
            } else {
                val role = baseMapper.selectById(id)
                role.takeIf { it != null }?.apply {
                    resourceIds = baseMapper.selectRelaResourceIds(id)
                }
                AjaxResult.success(role)
            }
        }
    }

    fun addRole(role: Role): AjaxResult {
        return if (baseMapper.isExist(role.roleId, role.perms) > 0) {
            AjaxResult.error("role has already exist")
        } else {
            role.roleId = UuidUtils.fastSimpleUUID()
            role.createTime = Date()
            val relaList = role.resourceIds.map {
                RoleRelaResource().apply {
                    id = UuidUtils.fastSimpleUUID()
                    roleId = role.roleId
                    resourceId = it
                }
            }.toList()
            relaList.takeIf { it.isNotEmpty() }?.let { baseMapper.batchSaveRela(it) }
            baseMapper.insert(role)
            return AjaxResult.success()
        }
    }

    fun updateRole(role: Role): AjaxResult {
        return if (baseMapper.isExist(role.roleId, role.perms) > 0) {
            AjaxResult.error("role has already exist")
        } else {
            baseMapper.removeRelaBatchByRoleIds(listOf(role.roleId))
            val relaList = role.resourceIds.map {
                RoleRelaResource().apply {
                    id = UuidUtils.fastSimpleUUID()
                    roleId = role.roleId
                    resourceId = it
                }
            }
            relaList.takeIf { it.isNotEmpty() }?.let { baseMapper.batchSaveRela(it) }
            AjaxResult.toAjax(baseMapper.updateById(role))
        }
    }

    @POST
    @Path("/save")
    @Transactional
    fun saveRole(@Valid role: Role): Uni<AjaxResult> {
        return uni {
            if (role.roleId.isBlank()) {
                addRole(role)
            } else {
                updateRole(role)
            }
        }
    }

    @POST
    @Path("/remove")
    @Transactional
    fun removeRole(ids: Ids): Uni<AjaxResult> {
        return uni {
            val list = ids.ids
            if (list.isEmpty()) {
                AjaxResult.success()
            } else {
                baseMapper.removeRelaBatchByRoleIds(list)
                baseMapper.removeBatchByIds(list)
                AjaxResult.success()
            }
        }
    }

    @POST
    @Path("/changeState")
    fun changeState(modifyState: ModifyState): Uni<AjaxResult> = uni { AjaxResult.toAjax(baseMapper.changeState(modifyState.id, modifyState.state)) }
}