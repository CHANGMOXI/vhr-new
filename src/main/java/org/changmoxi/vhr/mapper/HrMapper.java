package org.changmoxi.vhr.mapper;

import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.model.Role;

import java.util.List;

public interface HrMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Hr record);

    int insertSelective(Hr record);

    Hr selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Hr record);

    int updateByPrimaryKey(Hr record);

    Hr selectByUsername(String username);

    List<Role> getHrRolesById(Integer id);
}