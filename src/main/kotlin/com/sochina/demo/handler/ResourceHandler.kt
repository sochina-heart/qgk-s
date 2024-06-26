package com.sochina.demo.handler

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.sochina.demo.domain.Ids
import com.sochina.demo.domain.MenuItem
import com.sochina.demo.domain.ModifyState
import com.sochina.demo.domain.Resource
import com.sochina.demo.domain.ResourceVo
import com.sochina.demo.mapper.ResourceMapper
import com.sochina.demo.utils.encrypt.gm.sm4.SM4Utils
import com.sochina.demo.utils.uuid.UuidUtils
import com.sochina.demo.utils.web.AjaxResult
import io.quarkus.cache.CacheInvalidate
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
import java.util.Date
import java.util.logging.Logger

@Path("/resource")
class ResourceHandler(
    private val baseMapper: ResourceMapper,
    private val cacheHandler: CacheHandler,
) {

    private val logger: Logger = Logger.getLogger(ResourceHandler::class.java.name)

    @POST
    @Path("/list")
    fun listResource(resource: Resource): Uni<AjaxResult> {
        val queryWrapper = QueryWrapper<Resource>()
            .eq("delete_flag", "0")
            .eq("menu_type", resource.menuType)
            .eq("app_id", resource.appId)
            .apply {
                resource.resourceName.takeIf { it.isNotBlank() }?.let { like("resource_name", it) }
            }
            .select("resource_id", "resource_name", "app_id", "perms", "path", "state", "menu_type")
            .orderByDesc("update_time")
        val list =
            baseMapper.selectPage(
                resource.page?.let { Page(it.pageNumber.toLong(), it.pageSize.toLong()) },
                queryWrapper
            )
        return uni {
            AjaxResult.success(list)
        }
    }

    @GET
    @Path("/getTree")
    fun listResourceNoPage(
        @QueryParam("appId") appId: String,
        @QueryParam("menuType") menuType: String
    ): Uni<AjaxResult> {
        return uni {
            val queryWrapper = QueryWrapper<Resource>()
                .eq("app_id", appId)
                .eq("state", "0")
                .eq("delete_flag", "0")
                .also {
                    if (menuType == "F") {
                        it.`in`("menu_type", "M", "C")
                    } else if (menuType == "M" || menuType == "C") {
                        it.eq("menu_type", "M")
                    }
                }
                .select("resource_id", "resource_name", "parent_id", "order_num", "menu_type")
            val list = baseMapper.selectList(queryWrapper)
            AjaxResult.success(getResourceTree(list, "topzero"))
        }
    }

    private fun getResourceTree(list: List<Resource>, parentId: String): List<ResourceVo> {
        val result = mutableListOf<ResourceVo>()
        list.forEach {
            if (it.parentId == parentId) {
                val vo = ResourceVo()
                vo.id = it.resourceId
                vo.label = it.resourceName
                vo.type = it.menuType
                vo.children = getResourceTree(list, it.resourceId)
                result.add(vo)
            }
        }
        return result
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
        val permsList  = cacheHandler.getCachePerms(token)
        val list = baseMapper.getRouter(appId).filter { it.path in permsList }
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

    @POST
    @Path("/remove")
    fun removeResource(ids: Ids): Uni<AjaxResult> {
        return uni {
            if (ids.ids.isEmpty()) {
                AjaxResult.success()
            } else {
                AjaxResult.toAjax(baseMapper.removeBatchById(ids.ids))
            }
        }
    }

    fun addResource(resource: Resource): AjaxResult {
        resource.resourceId = UuidUtils.fastSimpleUUID()
        resource.createTime = Date()
        resource.deleteFlag = "0"
        return AjaxResult.success(baseMapper.insert(resource))
    }

    fun updateResource(resource: Resource): AjaxResult {
        if (resource.parentId == "topzero") {
            return AjaxResult.success()
        }
        return AjaxResult.toAjax(baseMapper.updateById(resource))
    }

    @POST
    @Path("/save")
    fun saveResource(@Valid resource: Resource): Uni<AjaxResult> {
        return uni {
            if (baseMapper.isExist(resource.resourceId, resource.perms) > 0) {
                logger.warning("resource ${resource.resourceName} has already exist")
                AjaxResult.success("resource has already")
            } else {
                if (resource.resourceId.isBlank()) {
                    addResource(resource)
                } else {
                    updateResource(resource)
                }
            }
        }
    }

    @POST
    @Path("/changeState")
    fun changeState(modifyState: ModifyState): Uni<AjaxResult> = uni { AjaxResult.toAjax(baseMapper.changeState(modifyState.id, modifyState.state)) }
}