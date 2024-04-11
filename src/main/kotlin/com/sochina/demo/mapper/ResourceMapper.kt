package com.sochina.demo.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.sochina.demo.domain.Resource
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ResourceMapper: BaseMapper<Resource> {

    fun changeState(resource: Resource): Int

    fun isExist(resource: Resource): Int

    fun removeBatchById(list: List<String>): Int
}