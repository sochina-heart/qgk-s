package com.sochina.demo.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.sochina.demo.domain.Application
import org.apache.ibatis.annotations.Mapper

@Mapper
interface ApplicationMapper: BaseMapper<Application> {

    fun appMap(): List<Map<String, String>>

    fun isExist(application: Application): Int
}