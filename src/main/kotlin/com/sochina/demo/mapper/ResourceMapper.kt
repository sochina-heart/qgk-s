package com.sochina.demo.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.sochina.demo.domain.Resource
import com.sochina.demo.domain.ResourceVo
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface ResourceMapper: BaseMapper<Resource> {

    fun changeState(id: String, state: String): Int

    fun isExist(resource: Resource): Int

    fun removeBatchById(list: List<String>): Int
}