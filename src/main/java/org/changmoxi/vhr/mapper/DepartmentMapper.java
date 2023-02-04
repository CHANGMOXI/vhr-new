package org.changmoxi.vhr.mapper;

import org.changmoxi.vhr.model.Department;

import java.util.List;

public interface DepartmentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Department record);

    int insertSelective(Department record);

    Department selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Department record);

    int updateByPrimaryKey(Department record);

    List<Department> getAllDepartmentsByParentId(Integer parentId);

    List<Integer> getParentIdsByName(String name);

    int addDepartmentAndReturnLastInsertId(Department department);

    int getEmployeeCountOfDepartment(Integer id);

    List<Department> getAllDepartments();
}