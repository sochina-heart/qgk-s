package com.sochina.demo.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.sochina.demo.domain.MenuItem
import com.sochina.demo.domain.SochinaUser
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SochinaUserMapper : BaseMapper<SochinaUser> {

    fun changeState(id: String, state: String): Int

    fun getPermsByUserId(id: String, appId: String): List<String>

    fun isExist(id: String, account: String): Int

    fun removeBatchById(list: List<String>): Int
}