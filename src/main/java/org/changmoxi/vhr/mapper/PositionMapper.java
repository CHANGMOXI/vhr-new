package org.changmoxi.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.changmoxi.vhr.model.Position;

import java.util.List;

public interface PositionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Position record);

    int insertSelective(Position record);

    Position selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Position record);

    int updateByPrimaryKey(Position record);

    List<Position> getAllPositions();

    Integer getPositionIdByName(String name);

    List<Integer> getExistEmployeePositionIdsByIds(Integer[] ids);

    int batchLogicDeletePositionsByIds(@Param("ids") Integer[] ids);
}