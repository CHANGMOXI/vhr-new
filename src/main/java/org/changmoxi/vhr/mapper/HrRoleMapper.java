package org.changmoxi.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.changmoxi.vhr.model.HrRole;

import java.util.List;

public interface HrRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HrRole record);

    int insertSelective(HrRole record);

    HrRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HrRole record);

    int updateByPrimaryKey(HrRole record);

    List<Integer> getAllRIdsByHrId(Integer hrId);

    int batchEnableOrDisableHrRoles(@Param("hrId") Integer hrId, @Param("rIds") Integer[] rIds, @Param("enable") Boolean enable);

    int insertHrRoles(@Param("hrId") Integer hrId, @Param("rIds") Integer[] rIds);
}