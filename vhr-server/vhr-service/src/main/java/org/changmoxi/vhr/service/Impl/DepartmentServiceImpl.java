package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.mapper.DepartmentMapper;
import org.changmoxi.vhr.mapper.SalaryMapper;
import org.changmoxi.vhr.model.Department;
import org.changmoxi.vhr.model.Salary;
import org.changmoxi.vhr.service.DepartmentService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-13 16:25
 **/
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private SalaryMapper salaryMapper;

    @Override
    @Cacheable(cacheNames = "department", key = "'all.departments'")
    public List<Department> getAllDepartments() {
        //递归(嵌套)查询
        return departmentMapper.getAllDepartmentsByParentId(-1);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    @Caching(evict = {@CacheEvict(cacheNames = "department", key = "'all.departments'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.fixedinfo'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.all.id.maps'")})
    public RespBean addDepartment(Department department) {
        if (ObjectUtils.anyNull(department, department.getParentId()) || StringUtils.isBlank(department.getName())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "department传参 或 name、parentId字段不能为空");
        }
        List<Integer> parentIdsList = departmentMapper.getParentIdsByName(department.getName());
        if (parentIdsList.contains(department.getParentId())) {
            throw new BusinessException(CustomizeStatusCode.EXIST_SAME_DEPARTMENT, "已存在同层级下的【" + department.getName() + "】部门");
        }

        int insertCount = departmentMapper.addDepartmentAndReturnLastInsertId(department);
        if (insertCount != 1) {
            //数据操作异常，手动抛异常回滚
            throw new PersistenceException("department表新增记录操作异常，name【" + department.getName() + "】parentId【" + department.getParentId() + "】");
        }

        Department parentDepartment = departmentMapper.selectByPrimaryKey(department.getParentId());
        Department updateDepartment = new Department();
        updateDepartment.setId(department.getId());
        updateDepartment.setDepPath(parentDepartment.getDepPath() + "." + department.getId());
        int updateCount = departmentMapper.updateByPrimaryKeySelective(updateDepartment);
        if (updateCount == 0) {
            //数据操作异常，手动抛异常回滚
            throw new PersistenceException("department表修改记录操作异常，id【" + department.getId() + "】depPath【" + updateDepartment.getDepPath() + "】");
        }

        if (!parentDepartment.getParent()) {
            Department updateParentDepartment = new Department();
            updateParentDepartment.setId(department.getParentId());
            updateParentDepartment.setParent(true);
            int updateParentCount = departmentMapper.updateByPrimaryKeySelective(updateParentDepartment);
            if (updateParentCount == 0) {
                //数据操作异常，手动抛异常回滚
                throw new PersistenceException("department表修改记录操作异常，id【" + department.getParentId() + "】isParent【" + updateParentDepartment.getParent() + "】");
            }
        }

        //返回新增的部门数据，方便前端直接加载新增的部门而不用重新请求所有部门数据
        Department returnDepartment = departmentMapper.selectByPrimaryKey(department.getId());
        returnDepartment.setChildren(new ArrayList<>());
        return RespBean.ok(CustomizeStatusCode.SUCCESS_ADD, returnDepartment);
    }

    @Override
    @Caching(evict = {@CacheEvict(cacheNames = "department", key = "'all.departments'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.fixedinfo'"),
            @CacheEvict(cacheNames = "salary", key = "'all.salaries'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.departmentid.to.salaryid.map'"),
            @CacheEvict(cacheNames = "employee", key = "'employee.all.id.maps'")})
    public RespBean deleteDepartment(Integer id) {
        if (Objects.isNull(id)) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "id不能为空");
        }

        Department department = departmentMapper.selectByPrimaryKey(id);
        if (department.getParent()) {
            throw new BusinessException(CustomizeStatusCode.EXIST_SUB_DEPARTMENTS, "【" + department.getName() + "】存在子部门");
        }
        int count = departmentMapper.getEmployeeCountOfDepartment(id);
        if (count > 0) {
            throw new BusinessException(CustomizeStatusCode.EXIST_EMPLOYEES, "【" + department.getName() + "】存在员工");
        }

        Department deleteDepartment = new Department();
        deleteDepartment.setId(id);
        deleteDepartment.setDeleted(true);
        int deleteCount = departmentMapper.updateByPrimaryKeySelective(deleteDepartment);
        if (deleteCount == 0) {
            //数据操作异常，手动抛异常回滚
            throw new PersistenceException("department表修改已有记录为删除操作异常，id【" + id + "】");
        }

        List<Department> subDepartments = departmentMapper.getAllDepartmentsByParentId(department.getParentId());
        if (CollectionUtils.isEmpty(subDepartments)) {
            Department updateParentDepartment = new Department();
            updateParentDepartment.setId(department.getParentId());
            updateParentDepartment.setParent(false);
            int updateCount = departmentMapper.updateByPrimaryKeySelective(updateParentDepartment);
            if (updateCount == 0) {
                //数据操作异常，手动抛异常回滚
                throw new PersistenceException("department表修改已有记录操作异常，id【" + department.getParentId() + "】");
            }
        }

        // 成功删除部门后，删除相应的工资账套
        Salary salary = new Salary();
        salary.setDepartmentId(id);
        salaryMapper.logicDeleteByDepartmentId(id);
        return RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE);
    }
}