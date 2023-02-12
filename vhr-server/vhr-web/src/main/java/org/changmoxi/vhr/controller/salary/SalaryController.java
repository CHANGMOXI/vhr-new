package org.changmoxi.vhr.controller.salary;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.model.Salary;
import org.changmoxi.vhr.service.SalaryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author CZS
 * @create 2023-02-11 0:54
 **/
@RestController
@RequestMapping("/salary/sob")
public class SalaryController {
    @Resource
    private SalaryService salaryService;

    /**
     * 获取所有工资账套数据
     *
     * @return
     */
    @GetMapping("/")
    public RespBean getAllSalaries() {
        return salaryService.getAllSalaries();
    }

    /**
     * 添加账套
     *
     * @param salary
     * @return
     */
    @PostMapping("/")
    public RespBean addSalary(@RequestBody @Valid Salary salary) {
        return salaryService.addSalary(salary);
    }

    /**
     * 删除账套
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public RespBean deleteSalary(@PathVariable Integer id) {
        return salaryService.deleteSalary(id);
    }

    /**
     * 更新账套
     *
     * @param salary
     * @return
     */
    @PutMapping("/")
    public RespBean updateSalary(@RequestBody @Valid Salary salary) {
        return salaryService.updateSalary(salary);
    }
}