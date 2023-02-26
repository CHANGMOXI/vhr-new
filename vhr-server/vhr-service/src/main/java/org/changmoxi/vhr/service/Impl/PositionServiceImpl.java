package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.mapper.PositionMapper;
import org.changmoxi.vhr.model.Position;
import org.changmoxi.vhr.service.PositionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(cacheNames = "position", key = "'all.positions'")
    public List<Position> getAllPositions() {
        return positionMapper.getAllPositions();
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "position", key = "'all.positions'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.all.id.maps'")})
    public RespBean addPosition(Position position) {
        if (Objects.isNull(position) || StringUtils.isBlank(position.getName())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "position传参不能为空 或 name字段不能为空");
        }
        if (Objects.nonNull(positionMapper.getPositionIdByName(position.getName()))) {
            throw new BusinessException(CustomizeStatusCode.EXIST_SAME_POSITION, "已存在【" + position.getName() + "】职位");
        }
        position.setCreateDate(new Date());
        position.setEnabled(true);
        position.setDeleted(false);
        return positionMapper.insertSelective(position) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "position", key = "'all.positions'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.all.id.maps'")})
    public RespBean updatePosition(Position position) {
        if (ObjectUtils.anyNull(position, position.getId(), position.getEnabled()) || StringUtils.isBlank(position.getName())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "position传参不能为空 或 id、name、enabled字段不能为空");
        }
        //name、enabled字段都相同就不更新
        Position selectPosition = positionMapper.selectByPrimaryKey(position.getId());
        if (StringUtils.equals(selectPosition.getName(), position.getName()) && selectPosition.getEnabled().equals(position.getEnabled())) {
            throw new BusinessException(CustomizeStatusCode.UPDATE_SAME_POSITION, "更新的【" + position.getName() + "】职位的name、enabled字段都相同");
        }
        position.setCreateDate(null);
        position.setDeleted(null);
        //只更新不为空的字段
        return positionMapper.updateByPrimaryKeySelective(position) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "position", key = "'all.positions'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.all.id.maps'")})
    public RespBean batchDeletePositions(Integer[] ids) {
        List<Integer> existEmployeePositionIdList = positionMapper.getExistEmployeePositionIdsByIds(ids);
        int size = existEmployeePositionIdList.size();
        if (size == ids.length) {
            if (ids.length == 1) {
                //该职位有员工(单个删除)
                throw new BusinessException(CustomizeStatusCode.EXIST_EMPLOYEES_WITH_THIS_POSITION, "该职位存在员工，不能删除该职位");
            }
            //所有职位都有员工
            throw new BusinessException(CustomizeStatusCode.EXIST_EMPLOYEES_WITH_ALL_POSITIONS, "所有职位都存在员工，不能删除这些职位");
        } else if (size > 0 && size < ids.length) {
            //部分职位有员工，只删除没有员工的职位
            ids = Arrays.stream(ids).filter(id -> !existEmployeePositionIdList.contains(id)).toArray(Integer[]::new);
        }

        return positionMapper.batchLogicDeletePositionsByIds(ids) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }
}