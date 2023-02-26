package org.changmoxi.vhr.service.Impl;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.mapper.SalaryMapper;
import org.changmoxi.vhr.model.Salary;
import org.changmoxi.vhr.service.SalaryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-02-11 0:57
 **/
@Service
public class SalaryServiceImpl implements SalaryService {
    @Resource
    private SalaryMapper salaryMapper;

    @Override
    @Cacheable(cacheNames = "salary", key = "'all.salaries'")
    public List<Salary> getAllSalaries() {
        return salaryMapper.getAllSalaries();
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "salary", key = "'all.salaries'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.departmentid.to.salaryid.map'")})
    public RespBean addSalary(Salary salary) {
        salary.setEnableDate(new Date());
        return salaryMapper.insertSelective(salary) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "salary", key = "'all.salaries'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.departmentid.to.salaryid.map'")})
    public RespBean deleteSalary(Integer id) {
        Salary salary = new Salary();
        salary.setId(id);
        salary.setDeleted(true);
        return salaryMapper.updateByPrimaryKeySelective(salary) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "salary", key = "'all.salaries'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.departmentid.to.salaryid.map'")})
    public RespBean updateSalary(Salary salary) {
        if (Objects.isNull(salary.getId())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "id字段不能为空");
        }
        return salaryMapper.updateByPrimaryKeySelective(salary) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }
}