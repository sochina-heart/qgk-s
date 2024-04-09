package com.sochina.demo.handler

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sochina.demo.domain.Ids
import com.sochina.demo.domain.Resource
import com.sochina.demo.mapper.ResourceMapper
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

@Path("/resource")
class ResourceHandler(
    private val baseMapper: ResourceMapper,
) {

    private val logger: Logger = Logger.getLogger(ResourceHandler::class.java.name)

    @POST
    @Path("/list")
    fun listResource(resource: Resource): Uni<AjaxResult> {
        val list =
            baseMapper.selectPage(
                resource.page?.let { Page(it.pageNumber.toLong(), it.pageSize.toLong()) },
                commonQuery(resource).select("resource_id", "resource_name", "perms")
            )
        return uni {
            AjaxResult.success(list)
        }
    }

    @GET
    @Path("/get/{id}")
    fun getResource(@PathParam("id") id: String): Uni<AjaxResult> {
        return uni {
            if (id.isBlank()) {
                logger.warning("resource id is empty")
                AjaxResult.error("resource id is empty")
            } else {
                AjaxResult.success(baseMapper.selectById(id))
            }
        }
    }

    @POST
    @Path("/remove")
    @Transactional
    fun removeResource(ids: Ids): Uni<AjaxResult> {
        return uni {
            if (ids.ids.isEmpty()) {
                AjaxResult.success()
            } else {
                ids.ids.forEach {
                    baseMapper.update(
                        UpdateWrapper<Resource>().set("delete_flag", "1").eq("resource_id", it)
                    )
                }
            }
            AjaxResult.success()
        }
    }

    fun addResource(resource: Resource): AjaxResult {
        resource.resourceId = UuidUtils.fastSimpleUUID()
        resource.createTime = Date()
        resource.deleteFlag = "0"
        return AjaxResult.success(baseMapper.insert(resource))
    }

    fun updateResource(resource: Resource): AjaxResult {
        return AjaxResult.toAjax(baseMapper.updateById(resource))
    }

    @POST
    @Path("/save")
    fun saveResource(@Valid resource: Resource): Uni<AjaxResult> {
        return uni {
            if (baseMapper.isExist(resource) > 0) {
                logger.warning("resource ${resource.resourceName} has already exist")
                AjaxResult.success("resource has already")
            } else {
                if (resource.resourceId.isNullOrBlank()) {
                    addResource(resource)
                } else {
                    updateResource(resource)
                }
            }
        }
    }

    private fun commonQuery(resource: Resource): QueryWrapper<Resource> {
        return QueryWrapper<Resource>()
            .eq("delete_flag", "0")
            .eq("menu_type", resource.menuType)
            .eq("app_id", resource.appId)
            .apply {
                resource.resourceName.takeIf { !it.isNullOrBlank() }?.let { like("resource_name", it) }
            }
            .orderByDesc("update_time")
    }
}