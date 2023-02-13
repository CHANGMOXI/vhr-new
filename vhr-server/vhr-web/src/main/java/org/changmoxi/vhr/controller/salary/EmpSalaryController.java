package org.changmoxi.vhr.controller.salary;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.service.EmployeeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author CZS
 * @create 2023-02-13 15:52
 **/
@RestController
@RequestMapping("/salary/sobcfg")
@Validated
public class EmpSalaryController {
    @Resource
    private EmployeeService employeeService;

    /**
     * 分页获取所有员工账套
     *
     * @return
     */
    @GetMapping("/")
    public RespBean getEmployeeSalaries(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize) {
        return employeeService.getEmployeeSalaries(pageNum, pageSize);
    }

    /**
     * 更新员工工资账套
     *
     * @param employeeId
     * @param salaryId
     * @return
     */
    @PutMapping("/")
    public RespBean updateEmployeeSalary(@Valid @NotNull(message = "员工id不能为空") @Min(value = 1, message = "员工id不能小于1") Integer employeeId,
                                         @Valid @NotNull(message = "工资账套id不能为空") @Min(value = 1, message = "工资账套id不能小于1") Integer salaryId) {
        return employeeService.updateEmployeeSalary(employeeId, salaryId);
    }
}
