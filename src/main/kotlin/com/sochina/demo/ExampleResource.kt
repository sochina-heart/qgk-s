package com.sochina.demo

import com.sochina.demo.mapper.SochinaUserMapper
import com.sochina.demo.utils.web.AjaxResult
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path

@Path("/hello")
class ExampleResource(
    private val baseMapper: SochinaUserMapper
) {
    @GET
    fun hello(): AjaxResult {
        return AjaxResult.success(baseMapper.selectById("0002ba0ce1df476e9a694fa2e0de0db3"))
    }
}