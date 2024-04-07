package com.sochina.demo.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.sochina.demo.domain.SochinaUser
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SochinaUserMapper : BaseMapper<SochinaUser> {

    fun isExist(sochinaUser: SochinaUser): Int
}