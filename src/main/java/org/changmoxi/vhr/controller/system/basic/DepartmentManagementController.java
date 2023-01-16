package org.changmoxi.vhr.controller.system.basic;

import org.changmoxi.vhr.model.Department;
import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author CZS
 * @create 2023-01-13 16:00
 **/
@RestController
@RequestMapping("/system/basic/departments")
public class DepartmentManagementController {
    @Resource
    private DepartmentService departmentService;

    /**
     * 获取所有部门数据
     *
     * @return
     */
    @GetMapping("/")
    public RespBean getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    /**
     * 添加部门
     *
     * @param department
     * @return
     */
    @PostMapping("/")
    public RespBean addDepartment(@RequestBody Department department) {
        return departmentService.addDepartment(department);
    }

    /**
     * 删除部门
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public RespBean deleteDepartment(@PathVariable Integer id) {
        return departmentService.deleteDepartment(id);
    }
}
