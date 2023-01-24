package org.changmoxi.vhr.service;

import org.changmoxi.vhr.model.Employee;
import org.changmoxi.vhr.model.RespBean;

/**
 * @author CZS
 * @create 2023-01-17 21:29
 **/
public interface EmployeeService {
    /**
     * 分页获取员工数据（带有姓名检索）
     *
     * @param pageNum
     * @param pageSize
     * @param keywords
     * @return
     */
    RespBean getEmployeesByPage(Integer pageNum, Integer pageSize, String keywords);

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
    RespBean getFixedInfo();

    /**
     * 获取职位信息
     *
     * @return
     */
    RespBean getPositions();

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
}
