package com.sochina.demo.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.sochina.demo.domain.Resource
import com.sochina.demo.domain.ResourceVo
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface ResourceMapper: BaseMapper<Resource> {

    fun changeState(resource: Resource): Int

    fun getTree(appId: String, menuType: String): List<ResourceVo>

    fun isExist(resource: Resource): Int

    fun removeBatchById(list: List<String>): Int
}