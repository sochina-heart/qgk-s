package com.sochina.demo.handler

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sochina.demo.domain.Application
import com.sochina.demo.domain.Ids
import com.sochina.demo.mapper.ApplicationMapper
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
    @Transactional
    fun removeApplication(ids: Ids): Uni<AjaxResult> {
        return uni {
            if (ids.ids.isEmpty()) {
                AjaxResult.success()
            } else {
                ids.ids.forEach {
                    baseMapper.update(
                        UpdateWrapper<Application>().set("delete_flag", "1").eq("app_id", it)
                    )
                }
            }
            AjaxResult.success()
        }
    }

    @POST
    @Path("/list")
    fun listApplication(application: Application): Uni<AjaxResult> {
        val list =
            baseMapper.selectPage(
                application.page?.let { Page(it.pageNumber.toLong(), it.pageSize.toLong()) },
                commonQuery(application).select("app_id", "app_name", "app_user", "app_email", "app_phone", "state")
            )
        return uni { AjaxResult.success(list) }
    }

    @POST
    @Path("/map")
    fun mapApplication(application: Application): Uni<AjaxResult> {
        return uni { AjaxResult.success(baseMapper.selectList(commonQuery(application).select("app_id", "app_name"))) }
    }

    fun addApp(application: Application): Uni<AjaxResult> {
        val count = baseMapper.selectCount(QueryWrapper<Application>().eq("app_name", application.appName))
        return uni {
            if (count > 0) {
                AjaxResult.error("application has already exist")
            } else {
                application.appId = UuidUtils.fastSimpleUUID()
                application.createTime = Date()
                application.deleteFlag = "0"
                AjaxResult.toAjax(baseMapper.insert(application))
            }
        }
    }

    fun updateApp(application: Application): Uni<AjaxResult> {
        val count = baseMapper.selectCount(QueryWrapper<Application>().eq("app_name", application.appName).notIn("app_id", application.appId))
        return uni {
            if (count > 0) {
                AjaxResult.error("application has already exist")
            } else {
                AjaxResult.toAjax(baseMapper.updateById(application))
            }
        }
    }

    @POST
    @Path("/save")
    fun saveApplication(@Valid application: Application): Uni<AjaxResult> {
        return if (application.appId.isNullOrBlank()) {
            addApp(application)
        } else {
            updateApp(application)
        }
    }

    private fun commonQuery(application: Application): QueryWrapper<Application> {
        return QueryWrapper<Application>()
            .eq("delete_flag", "0")
            .apply {
                application.state?.let { eq("state", it) }
                application.appName?.let { like("app_name", it) }
                application.appUser?.let { like("app_user", it) }
            }
            .orderByDesc("update_time")
    }
}