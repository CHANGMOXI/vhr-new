package org.changmoxi.vhr.controller.emp;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import org.changmoxi.vhr.config.EmployeeImportListener;
import org.changmoxi.vhr.dto.EmployeeExportDTO;
import org.changmoxi.vhr.dto.EmployeeImportDTO;
import org.changmoxi.vhr.dto.EmployeeSearchDTO;
import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.mapper.EmployeeMapper;
import org.changmoxi.vhr.model.Employee;
import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.service.EmployeeService;
import org.changmoxi.vhr.utils.EasyExcelUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author CZS
 * @create 2023-01-17 18:46
 **/
@RestController
@RequestMapping("/employee/basic")
public class EmployeeBasicController {
    @Resource
    private EmployeeService employeeService;

    @Resource
    private EmployeeMapper employeeMapper;

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
        List<Employee> employees = employeeService.getEmployeesByPage(pageNum, pageSize, employeeSearchDTO);
        PageInfo<Employee> pageInfo = new PageInfo<>(employees);
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
        return employeeService.addEmployee(employee);
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
        EmployeeImportListener employeeImportListener = new EmployeeImportListener(employeeMapper, allIdMaps, file.getOriginalFilename());
        EasyExcel.read(file.getInputStream(), EmployeeImportDTO.class, employeeImportListener).sheet().doRead();
        if (CollectionUtils.isEmpty(employeeImportListener.getErrorDataList())) {
            return RespBean.ok("导入成功!");
        } else {
            // TODO 导出错误员工数据收集表，可以在前端新建一个页面，接收并管理服务端存储的错误数据收集表，服务端收集位置: vhr-new\error-data-files
            return RespBean.ok("导入成功，部分错误员工数据未导入!");
        }
    }
}