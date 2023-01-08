package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.exception.CustomizeException;
import org.changmoxi.vhr.mapper.PositionMapper;
import org.changmoxi.vhr.model.Position;
import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.service.PositionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
        if (positionMapper.getCountByName(position.getName()) > 0) {
            throw new CustomizeException(CustomizeStatusCode.EXIST_SAME_POSITION, "已存在【" + position.getName() + "】职位");
        }
        position.setCreateDate(new Date());
        position.setEnabled(true);
        position.setDeleted(false);
        return positionMapper.insertSelective(position) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    @Override
    public RespBean updatePosition(Position position) {
        if (Objects.isNull(position) || Objects.isNull(position.getId()) || StringUtils.isBlank(position.getName()) ||
                (Objects.isNull(position.getCreateDate()) && Objects.isNull(position.getEnabled()) && Objects.isNull(position.getDeleted()))) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "position传参不能为空 或 id不能为空 或 id和name以外的字段不能全为空");
        }
        //职位名称相同就不更新
        String name = positionMapper.getNameById(position.getId());
        if (position.getName().equals(name)) {
            throw new CustomizeException(CustomizeStatusCode.UPDATE_SAME_POSITION_NAME, "更新的职位名称【" + position.getName() + "】和原有职位名称相同");
        }
        //只更新不为空的字段
        return positionMapper.updateByPrimaryKeySelective(position) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    public RespBean deletePosition(Integer id) {
        if (Objects.isNull(id)) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "id不能为空!");
        }

        Integer[] ids = new Integer[1];
        ids[0] = id;
        //TODO 查询方法可能还需要考虑到以后可能会给employee表添加的deleted字段
        List<Integer> existEmployeePositionIdList = positionMapper.getExistEmployeePositionIdsByIds(ids);
        if (!CollectionUtils.isEmpty(existEmployeePositionIdList)) {
            throw new CustomizeException(CustomizeStatusCode.EXIST_EMPLOYEES_WITH_THIS_POSITION, "该职位存在员工，不能删除该职位");
        }

        Position position = new Position();
        position.setId(id);
        position.setDeleted(true);
        return positionMapper.updateByPrimaryKeySelective(position) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }

    @Override
    public RespBean batchDeletePositions(Integer[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "ids数组不能为空");
        }

        //TODO 查询方法可能还需要考虑到以后可能会给employee表添加的deleted字段
        List<Integer> existEmployeePositionIdList = positionMapper.getExistEmployeePositionIdsByIds(ids);
        int size = existEmployeePositionIdList.size();
        if (size == ids.length) {
            //所有职位都有员工
            throw new CustomizeException(CustomizeStatusCode.EXIST_EMPLOYEES_WITH_ALL_POSITIONS, "所有职位都存在员工，不能删除这些职位");
        } else if (size > 0 && size < ids.length) {
            //部分职位有员工，只删除没有员工的职位
            ids = Arrays.stream(ids).filter(id -> !existEmployeePositionIdList.contains(id)).toArray(Integer[]::new);
        }

        return positionMapper.batchUpdatePositionsByIds(ids) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }
}
