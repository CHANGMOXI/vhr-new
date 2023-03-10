package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.mapper.JobLevelMapper;
import org.changmoxi.vhr.model.JobLevel;
import org.changmoxi.vhr.service.JobLevelService;
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
 * @create 2023-01-08 17:12
 **/
@Service
public class JobLevelServiceImpl implements JobLevelService {
    @Resource
    private JobLevelMapper jobLevelMapper;

    @Override
    @Cacheable(cacheNames = "joblevel", key = "'all.joblevels'")
    public List<JobLevel> getAllJobLevels() {
        return jobLevelMapper.getAllJobLevels();
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "joblevel", key = "'all.joblevels'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.fixedinfo'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.all.id.maps'")})
    public RespBean addJobLevel(JobLevel jobLevel) {
        if (Objects.isNull(jobLevel) || StringUtils.isAnyBlank(jobLevel.getName(), jobLevel.getTitleLevel())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "jobLevel传参不能为空 或 name字段或titleLevel字段不能为空");
        }
        if (Objects.nonNull(jobLevelMapper.getJobLevelIdByName(jobLevel.getName()))) {
            throw new BusinessException(CustomizeStatusCode.EXIST_SAME_JOBLEVEL, "已存在【" + jobLevel.getName() + "】职称");
        }
        jobLevel.setCreateDate(new Date());
        jobLevel.setEnabled(true);
        jobLevel.setDeleted(false);
        return jobLevelMapper.insertSelective(jobLevel) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "joblevel", key = "'all.joblevels'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.fixedinfo'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.all.id.maps'")})
    public RespBean updateJobLevel(JobLevel jobLevel) {
        if (ObjectUtils.anyNull(jobLevel, jobLevel.getId(), jobLevel.getEnabled()) || StringUtils.isAnyBlank(jobLevel.getName(), jobLevel.getTitleLevel())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "position传参 或 id、name、titleLevel、enabled字段不能为空");
        }
        //name、titleLevel、enabled字段都相同就不更新
        JobLevel selectJobLevel = jobLevelMapper.selectByPrimaryKey(jobLevel.getId());
        if (StringUtils.equals(selectJobLevel.getName(), jobLevel.getName())
                && StringUtils.equals(selectJobLevel.getTitleLevel(), jobLevel.getTitleLevel())
                && selectJobLevel.getEnabled().equals(jobLevel.getEnabled())) {
            throw new BusinessException(CustomizeStatusCode.UPDATE_SAME_JOBLEVEL, "更新的【" + jobLevel.getName() + "】职称的name、titleLevel、enabled字段都相同");
        }
        jobLevel.setCreateDate(null);
        jobLevel.setDeleted(null);
        //只更新不为空的字段
        return jobLevelMapper.updateByPrimaryKeySelective(jobLevel) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "joblevel", key = "'all.joblevels'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.fixedinfo'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.all.id.maps'")})
    public RespBean batchDeleteJobLevels(Integer[] ids) {
        List<Integer> existEmployeeJobLevelIdList = jobLevelMapper.getExistEmployeeJobLevelIdsByIds(ids);
        int size = existEmployeeJobLevelIdList.size();
        if (size == ids.length) {
            if (ids.length == 1) {
                //该职称有员工(单个删除)
                throw new BusinessException(CustomizeStatusCode.EXIST_EMPLOYEES_WITH_THIS_JOBLEVEL, "该职称存在员工，不能删除该职称");
            }
            //所有职称都有员工
            throw new BusinessException(CustomizeStatusCode.EXIST_EMPLOYEES_WITH_ALL_POSITIONS, "所有职称都存在员工，不能删除这些职称");
        } else if (size > 0 && size < ids.length) {
            //部分职称有员工，只删除没有员工的职称
            ids = Arrays.stream(ids).filter(id -> !existEmployeeJobLevelIdList.contains(id)).toArray(Integer[]::new);
        }

        return jobLevelMapper.batchLogicDeleteJobLevelsByIds(ids) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }
}