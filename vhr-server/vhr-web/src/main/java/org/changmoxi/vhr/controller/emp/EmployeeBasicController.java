package org.changmoxi.vhr.controller.emp;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.utils.EasyExcelUtil;
import org.changmoxi.vhr.dto.EmployeeExportDTO;
import org.changmoxi.vhr.dto.EmployeeImportDTO;
import org.changmoxi.vhr.dto.EmployeeSearchDTO;
import org.changmoxi.vhr.model.Employee;
import org.changmoxi.vhr.service.EmployeeService;
import org.changmoxi.vhr.service.listener.EmployeeImportListener;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-17 18:46
 **/
@RestController
@RequestMapping("/employee/basic")
public class EmployeeBasicController {
    @Resource
    private EmployeeService employeeService;

    private static final Integer[] PAGE_SIZES = new Integer[]{10, 20, 30, 40, 50, 100};

    /**
     * 分页获取员工数据（带检索）
     *
     * @param pageNum
     * @param pageSize
     * @param employeeSearchDTO
     * @return
     */
    @GetMapping("/")
    public RespBean getEmployeesByPage(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize, EmployeeSearchDTO employeeSearchDTO) {
        PageInfo<Employee> pageInfo = Objects.isNull(employeeSearchDTO) || employeeSearchDTO.isAllNull() ?
                employeeService.getEmployeesByPage(pageNum, pageSize) :
                employeeService.getEmployeesByPageAndSearch(pageNum, pageSize, employeeSearchDTO);
        return RespBean.page(CustomizeStatusCode.SUCCESS, pageInfo);
    }

    /**
     * 添加员工
     *
     * @param employee
     * @return
     */
    @PostMapping("/")
    public RespBean addEmployee(@RequestBody @Valid Employee employee) {
        Map<Integer, Integer> departmentIdToSalaryIdMap = employeeService.getDepartmentIdToSalaryIdMap();
        if (departmentIdToSalaryIdMap.containsKey(employee.getDepartmentId())) {
            employee.setSalaryId(departmentIdToSalaryIdMap.get(employee.getDepartmentId()));
        }
        int insertCount = employeeService.addEmployee(employee);
        if (insertCount == 1) {
            // 计算员工的分页位置，删除相应的分页缓存
            Integer count = employeeService.getCountLessThanId(employee.getId());
            for (Integer pageSize : PAGE_SIZES) {
                Integer pageNum = count / pageSize + 1;
                employeeService.deleteEmployeesPageCache(pageNum, pageSize);
            }
        }
        return insertCount == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    /**
     * 获取员工信息中的较少变化的信息
     *
     * @return
     */
    @GetMapping("/fixed_info")
    public RespBean getFixedInfo() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, employeeService.getFixedInfo());
    }

    /**
     * 获取职位信息
     *
     * @return
     */
    @GetMapping("/positions")
    public RespBean getPositions() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, employeeService.getPositions());
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
     * 删除员工
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public RespBean deleteEmployee(@PathVariable Integer id) {
        int deleteCount = employeeService.deleteEmployee(id);
        if (deleteCount == 1) {
            // 删除员工后，该员工后面的所有员工涉及的分页缓存都应该删除，因此直接清空所有分页缓存
            employeeService.clearAllPageCache();
        }
        return deleteCount == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }

    /**
     * 更新员工
     *
     * @param employee
     * @return
     */
    @PutMapping("/")
    public RespBean updateEmployee(@RequestBody @Valid Employee employee) {
        int updateCount = employeeService.updateEmployee(employee);
        if (updateCount == 1) {
            // 计算员工的分页位置，删除相应的分页缓存
            Integer count = employeeService.getCountLessThanId(employee.getId());
            for (Integer pageSize : PAGE_SIZES) {
                Integer pageNum = count / pageSize + 1;
                employeeService.deleteEmployeesPageCache(pageNum, pageSize);
            }
        }
        return updateCount == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    /**
     * 导出员工数据Excel表，失败返回JSON提示
     *
     * @param response
     * @param startPage
     * @param endPage
     * @param pageSize
     * @throws IOException
     */
    @GetMapping("/export")
    public void exportData(HttpServletResponse response,
                           @RequestParam(defaultValue = "1") Integer startPage,
                           @RequestParam(defaultValue = "1") Integer endPage,
                           @RequestParam(defaultValue = "10") Integer pageSize) throws IOException {
        List<EmployeeExportDTO> employees = employeeService.getExportEmployeesByPage(startPage, endPage, pageSize);
        EasyExcelUtil.webWriteFailedReturnJson(response, "员工数据导出表", "员工数据", EmployeeExportDTO.class, employees, CustomizeStatusCode.ERROR_EXPORT);
    }

    /**
     * 导入员工数据
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("import")
    public RespBean importData(MultipartFile file) throws IOException {
        Map<String, Map<String, Integer>> allIdMaps = employeeService.getAllIdMaps();
        EmployeeImportListener employeeImportListener = new EmployeeImportListener(employeeService, allIdMaps, file.getOriginalFilename());
        EasyExcel.read(file.getInputStream(), EmployeeImportDTO.class, employeeImportListener).sheet().doRead();
        if (CollectionUtils.isEmpty(employeeImportListener.getErrorDataList())) {
            return RespBean.ok("导入成功!");
        } else {
            // TODO 导出错误员工数据收集表，可以在前端新建一个页面，接收并管理服务端存储的错误数据收集表，服务端收集位置: vhr-new\error-data-files
            return RespBean.ok("导入成功，部分错误员工数据未导入!");
        }
    }
}