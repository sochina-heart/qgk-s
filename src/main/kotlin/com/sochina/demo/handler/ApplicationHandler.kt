package com.sochina.demo.handler

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sochina.demo.domain.Application
import com.sochina.demo.domain.Ids
import com.sochina.demo.domain.ModifyState
import com.sochina.demo.mapper.ApplicationMapper
import com.sochina.demo.utils.uuid.UuidUtils
import com.sochina.demo.utils.web.AjaxResult
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import jakarta.validation.Valid
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import java.util.Date
import java.util.logging.Logger

@Path("/app")
class ApplicationHandler(
    private val baseMapper: ApplicationMapper,
) {

    private val logger: Logger = Logger.getLogger(ApplicationHandler::class.java.name)

    @GET
    @Path("/get/{id}")
    fun getApplication(@PathParam("id") id: String): Uni<AjaxResult> {
        return uni {
            if (id.isBlank()) {
                logger.info("application id is $id")
                AjaxResult.error("application id is empty")
            } else {
                AjaxResult.success(baseMapper.selectById(id))
            }
        }
    }

    @POST
    @Path("/remove")
    fun removeApplication(ids: Ids): Uni<AjaxResult> {
        return uni {
            if (ids.ids.isEmpty()) {
                AjaxResult.success()
            } else {
                AjaxResult.toAjax(baseMapper.removeBatchById(ids.ids))
            }
        }
    }

    @POST
    @Path("/list")
    fun listApplication(application: Application): Uni<AjaxResult> {
        val queryWrapper = QueryWrapper<Application>()
            .eq("delete_flag", "0")
            .apply {
                application.state.takeIf { it.isNotBlank() }?.let { eq("state", it) }
                application.appName.takeIf { it.isNotBlank() }?.let { like("app_name", it) }
            }
            .select("app_id", "app_name", "app_user", "app_email", "app_phone", "state")
            .orderByDesc("update_time")
        val list =
            baseMapper.selectPage(
                application.page?.let { Page(it.pageNumber.toLong(), it.pageSize.toLong()) },
                queryWrapper
            )
        return uni { AjaxResult.success(list) }
    }

    @POST
    @Path("/map")
    fun mapApplication(): Uni<AjaxResult> {
        return uni { AjaxResult.success(baseMapper.appMap()) }
    }

    fun addApp(application: Application): AjaxResult {
        application.appId = UuidUtils.fastSimpleUUID()
        application.createTime = Date()
        application.deleteFlag = "0"
        return AjaxResult.toAjax(baseMapper.insert(application))
    }

    fun updateApp(application: Application): AjaxResult {
        return AjaxResult.toAjax(baseMapper.updateById(application))
    }

    @POST
    @Path("/save")
    fun saveApplication(@Valid application: Application): Uni<AjaxResult> {
        return uni {
            if (baseMapper.isExist(application.appId, application.perms) > 0) {
                AjaxResult.error("application has already exist")
            } else {
                if (application.appId.isBlank()) {
                    addApp(application)
                } else {
                    updateApp(application)
                }
            }
        }
    }

    @POST
    @Path("/changeState")
    fun changeState(modifyState: ModifyState): Uni<AjaxResult> = uni { AjaxResult.toAjax(baseMapper.changeState(modifyState.id, modifyState.state)) }
}