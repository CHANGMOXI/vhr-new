package org.changmoxi.vhr.service;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.model.Department;

/**
 * @author CZS
 * @create 2023-01-13 16:25
 **/
public interface DepartmentService {
    /**
     * 获取所有部门数据
     *
     * @return
     */
    RespBean getAllDepartments();

    /**
     * 添加部门
     *
     * @param department
     * @return
     */
    RespBean addDepartment(Department department);

    /**
     * 删除部门
     *
     * @param id
     * @return
     */
    RespBean deleteDepartment(Integer id);
}
