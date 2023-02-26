package org.changmoxi.vhr.service;

import com.github.pagehelper.PageInfo;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.dto.EmployeeExportDTO;
import org.changmoxi.vhr.dto.EmployeeImportDTO;
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
     * 分页获取员工数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Employee> getEmployeesByPage(Integer pageNum, Integer pageSize);

    /**
     * 分页获取员工数据（带检索）
     *
     * @param pageNum
     * @param pageSize
     * @param employeeSearchDTO
     * @return
     */
    PageInfo<Employee> getEmployeesByPageAndSearch(Integer pageNum, Integer pageSize, EmployeeSearchDTO employeeSearchDTO);

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    int addEmployee(Employee employee);

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
    int deleteEmployee(Integer id);

    /**
     * 更新员工
     *
     * @param employee
     * @return
     */
    int updateEmployee(Employee employee);

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

    /**
     * 导入员工数据批量入库
     *
     * @param importEmployees
     */
    void saveImportEmployees(List<EmployeeImportDTO> importEmployees);

    /**
     * 获取部门id与工资账套id的Map
     *
     * @return
     */
    Map<Integer, Integer> getDepartmentIdToSalaryIdMap();

    /**
     * 分页获取所有员工账套
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<Employee> getEmployeeSalaries(Integer pageNum, Integer pageSize);

    /**
     * 更新员工工资账套
     *
     * @param employeeId
     * @param salaryId
     * @return
     */
    int updateEmployeeSalary(Integer employeeId, Integer salaryId);

    /**
     * 获取该员工前面的员工数，用于计算分页位置
     *
     * @param id
     * @return
     */
    Integer getCountLessThanId(Integer id);

    /**
     * 删除对应分页缓存
     * 使用@CacheEvict注解的注意点: 需要在Controller层直接调用，方法返回值存在或者void(都会忽略)，并且是在实现类中被重写的方法（走代理的方法），注解才能生效，在Service层间接调用不会生效
     *
     * @param pageNum
     * @param pageSize
     */
    void deleteEmployeesPageCache(Integer pageNum, Integer pageSize);

    /**
     * 删除对应分页缓存
     * 使用@CacheEvict注解的注意点: 需要在Controller层直接调用，方法返回值存在或者void(都会忽略)，并且是在实现类中被重写的方法（走代理的方法），注解才能生效，在Service层间接调用不会生效
     *
     * @param pageNum
     * @param pageSize
     */
    void deleteEmployeeSalariesPageCache(Integer pageNum, Integer pageSize);

    /**
     * 清空所有员工分页缓存
     */
    void clearAllPageCache();
}