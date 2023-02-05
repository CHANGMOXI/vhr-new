package org.changmoxi.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.changmoxi.vhr.model.MenuRole;

import java.util.List;

public interface MenuRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MenuRole record);

    int insertSelective(MenuRole record);

    MenuRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MenuRole record);

    int updateByPrimaryKey(MenuRole record);

    List<Integer> getEnabledMidsByRid(Integer rid);

    List<Integer> getAllMIdsByRId(Integer rId);

    int batchEnableOrDisableMenuRoles(@Param("rId") Integer rId, @Param("mIds") Integer[] mIds, @Param("enable") Boolean enable);

    int insertMenuRoles(@Param("rId") Integer rId, @Param("mIds") Integer[] mIds);
}