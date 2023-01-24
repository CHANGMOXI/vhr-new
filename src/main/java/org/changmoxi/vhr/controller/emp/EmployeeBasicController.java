package org.changmoxi.vhr.controller.emp;

import org.changmoxi.vhr.model.Employee;
import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author CZS
 * @create 2023-01-17 18:46
 **/
@RestController
@RequestMapping("/employee/basic")
public class EmployeeBasicController {
    @Resource
    private EmployeeService employeeService;

    /**
     * 分页获取员工数据（带有姓名检索）
     *
     * @param pageNum
     * @param pageSize
     * @param keywords
     * @return
     */
    @GetMapping("/")
    public RespBean getEmployeesByPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, String keywords) {
        return employeeService.getEmployeesByPage(pageNum, pageSize, keywords);
    }

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @PostMapping("/")
    public RespBean addEmployee(@RequestBody @Valid Employee employee) {
        return employeeService.addEmployee(employee);
    }

    /**
     * 获取员工信息中的较少变化的信息
     *
     * @return
     */
    @GetMapping("/fixed_info")
    public RespBean getFixedInfo() {
        return employeeService.getFixedInfo();
    }

    /**
     * 获取职位信息
     *
     * @return
     */
    @GetMapping("/positions")
    public RespBean getPositions() {
        return employeeService.getPositions();
    }

    /**
     * 获取添加员工的最新工号
     *
     * @return
     */
    @GetMapping("/next_work_id")
    public RespBean getNextWorkId() {
        return employeeService.getNextWorkId();
    }

    /**
     * 删除部门
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public RespBean deleteEmployee(@PathVariable Integer id) {
        return employeeService.deleteEmployee(id);
    }

    /**
     * 更新员工
     *
     * @param employee
     * @return
     */
    @PutMapping("/")
    public RespBean updateEmployee(@RequestBody @Valid Employee employee) {
        return employeeService.updateEmployee(employee);
    }
}
