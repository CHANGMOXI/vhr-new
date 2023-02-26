package org.changmoxi.vhr.service;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.model.Salary;

import java.util.List;

/**
 * @author CZS
 * @create 2023-02-11 0:57
 **/
public interface SalaryService {
    /**
     * 获取所有工资账套数据
     *
     * @return
     */
    List<Salary> getAllSalaries();

    /**
     * 添加账套
     *
     * @param salary
     * @return
     */
    RespBean addSalary(Salary salary);

    /**
     * 删除账套
     *
     * @param id
     * @return
     */
    RespBean deleteSalary(Integer id);

    /**
     * 更新账套
     *
     * @param salary
     * @return
     */
    RespBean updateSalary(Salary salary);
}