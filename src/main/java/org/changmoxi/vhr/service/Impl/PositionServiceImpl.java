package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.exception.CustomizeException;
import org.changmoxi.vhr.mapper.PositionMapper;
import org.changmoxi.vhr.model.Position;
import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.service.PositionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-06 16:43
 **/
@Service
public class PositionServiceImpl implements PositionService {
    @Resource
    private PositionMapper positionMapper;

    @Override
    public RespBean getAllPositions() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, positionMapper.getAllPositions());
    }

    @Override
    public RespBean addPosition(Position position) {
        if (Objects.isNull(position) || StringUtils.isBlank(position.getName())) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "position传参不能为空 或 name字段不能为空");
        }
        if (Objects.nonNull(positionMapper.getPositionIdByName(position.getName()))) {
            throw new CustomizeException(CustomizeStatusCode.EXIST_SAME_POSITION, "已存在【" + position.getName() + "】职位");
        }
        position.setCreateDate(new Date());
        position.setEnabled(true);
        position.setDeleted(false);
        return positionMapper.insertSelective(position) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    @Override
    public RespBean updatePosition(Position position) {
        if (Objects.isNull(position) || Objects.isNull(position.getId())
                || StringUtils.isBlank(position.getName()) || Objects.isNull(position.getEnabled())) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "position传参不能为空 或 id、name、enabled字段不能为空");
        }
        //name、enabled字段都相同就不更新
        Position selectPosition = positionMapper.selectByPrimaryKey(position.getId());
        if (selectPosition.getName().equals(position.getName()) && selectPosition.getEnabled().equals(position.getEnabled())) {
            throw new CustomizeException(CustomizeStatusCode.UPDATE_SAME_POSITION, "更新的【" + position.getName() + "】职位的name、enabled字段都相同");
        }
        position.setCreateDate(null);
        position.setDeleted(null);
        //只更新不为空的字段
        return positionMapper.updateByPrimaryKeySelective(position) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    public RespBean batchDeletePositions(Integer[] ids) {
        //TODO 查询方法可能还需要考虑到以后可能会给employee表添加的deleted字段
        List<Integer> existEmployeePositionIdList = positionMapper.getExistEmployeePositionIdsByIds(ids);
        int size = existEmployeePositionIdList.size();
        if (size == ids.length) {
            if (ids.length == 1) {
                //该职位有员工(单个删除)
                throw new CustomizeException(CustomizeStatusCode.EXIST_EMPLOYEES_WITH_THIS_POSITION, "该职位存在员工，不能删除该职位");
            }
            //所有职位都有员工
            throw new CustomizeException(CustomizeStatusCode.EXIST_EMPLOYEES_WITH_ALL_POSITIONS, "所有职位都存在员工，不能删除这些职位");
        } else if (size > 0 && size < ids.length) {
            //部分职位有员工，只删除没有员工的职位
            ids = Arrays.stream(ids).filter(id -> !existEmployeePositionIdList.contains(id)).toArray(Integer[]::new);
        }

        return positionMapper.batchLogicDeletePositionsByIds(ids) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }
}
