package com.sochina.demo.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import com.sochina.demo.domain.Role
import com.sochina.demo.domain.RoleRelaResource
import org.apache.ibatis.annotations.Mapper

@Mapper
interface RoleMapper: BaseMapper<Role> {

    fun batchSaveRela(list: List<RoleRelaResource>): Int

    fun changeState(id: String, state: String): Int

    fun isExist(roleName: String, appId: String): Int

    fun removeBatchByIds(list: List<String>): Int

    fun removeRelaBatchByRoleIds(list: List<String>): Int
}