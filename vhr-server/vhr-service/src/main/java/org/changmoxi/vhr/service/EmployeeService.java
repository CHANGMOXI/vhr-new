package org.changmoxi.vhr.service;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.dto.EmployeeExportDTO;
import org.changmoxi.vhr.dto.EmployeeSearchDTO;
import org.changmoxi.vhr.model.Employee;
import org.changmoxi.vhr.model.Position;

import java.util.List;
import java.util.Map;

/**
 * @author CZS
 * @create 2023-01-17 21:29
 **/
public interface EmployeeService {
    /**
     * 分页获取员工数据（带检索）
     *
     * @param pageNum
     * @param pageSize
     * @param employeeSearchDTO
     * @return
     */
    List<Employee> getEmployeesByPage(Integer pageNum, Integer pageSize, EmployeeSearchDTO employeeSearchDTO);

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    RespBean addEmployee(Employee employee);

    /**
     * 获取员工信息中的较少变化的信息
     *
     * @return
     */
    Map<String, List<?>> getFixedInfo();

    /**
     * 获取职位信息
     *
     * @return
     */
    List<Position> getPositions();

    /**
     * 获取添加员工的最新工号
     *
     * @return
     */
    RespBean getNextWorkId();

    /**
     * 删除部门
     *
     * @param id
     * @return
     */
    RespBean deleteEmployee(Integer id);

    /**
     * 更新员工
     *
     * @param employee
     * @return
     */
    RespBean updateEmployee(Employee employee);

    /**
     * 获取指定范围的员工数据(导出)
     *
     * @param startPage
     * @param endPage
     * @param pageSize
     * @return
     */
    List<EmployeeExportDTO> getExportEmployeesByPage(Integer startPage, Integer endPage, Integer pageSize);

    /**
     * 获取员工的民族、政治面貌、部门、职位、职称的Id集合
     *
     * @return
     */
    Map<String, Map<String, Integer>> getAllIdMaps();
}