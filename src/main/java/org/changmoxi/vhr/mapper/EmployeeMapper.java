package org.changmoxi.vhr.mapper;

import org.apache.ibatis.annotations.Param;
import org.changmoxi.vhr.dto.EmployeeExportDTO;
import org.changmoxi.vhr.dto.EmployeeImportDTO;
import org.changmoxi.vhr.dto.EmployeeSearchDTO;
import org.changmoxi.vhr.model.Employee;

import java.util.List;

public interface EmployeeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Employee record);

    int insertSelective(Employee record);

    Employee selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Employee record);

    int updateByPrimaryKey(Employee record);

    List<Employee> getEmployees(@Param("search") EmployeeSearchDTO employeeSearchDTO);

    List<EmployeeExportDTO> getExportEmployeesByPage(@Param("search") EmployeeSearchDTO employeeSearchDTO, @Param("offset") Integer offset, @Param("exportPageSize") Integer exportPageSize);

    Integer getMaxWorkId();

    Integer getEmployeesTotalCount();

    int batchInsertEmployees(List<EmployeeImportDTO> employees);
}