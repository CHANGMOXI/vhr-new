package org.changmoxi.vhr.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.CustomizeException;
import org.changmoxi.vhr.common.message.basic.MailProducer;
import org.changmoxi.vhr.dto.EmployeeExportDTO;
import org.changmoxi.vhr.dto.EmployeeImportDTO;
import org.changmoxi.vhr.dto.EmployeeMailDTO;
import org.changmoxi.vhr.dto.EmployeeSearchDTO;
import org.changmoxi.vhr.mapper.*;
import org.changmoxi.vhr.model.*;
import org.changmoxi.vhr.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author CZS
 * @create 2023-01-17 21:29
 **/
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private NationMapper nationMapper;

    @Resource
    private PoliticsStatusMapper politicsStatusMapper;

    @Resource
    private JobLevelMapper jobLevelMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private PositionMapper positionMapper;

    @Resource
    private SalaryMapper salaryMapper;

    @Resource
    private MailProducer mailProducer;

    @Override
    public RespBean getEmployeesByPage(Integer pageNum, Integer pageSize, EmployeeSearchDTO employeeSearchDTO) {
        if (ArrayUtils.isNotEmpty(employeeSearchDTO.getEmploymentDateScope()) && employeeSearchDTO.getEmploymentDateScope().length == 1) {
            if (StringUtils.equals("null", employeeSearchDTO.getEmploymentDateScope()[0])) {
                employeeSearchDTO.setEmploymentDateScope(null);
            } else {
                String[] employmentDateScope = new String[2];
                employmentDateScope[0] = employeeSearchDTO.getEmploymentDateScope()[0];
                employmentDateScope[1] = employeeSearchDTO.getEmploymentDateScope()[0];
                employeeSearchDTO.setEmploymentDateScope(employmentDateScope);
            }
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> employees = employeeMapper.getEmployees(employeeSearchDTO);
        PageInfo<Employee> pageInfo = new PageInfo<>(employees);
        return RespBean.page(CustomizeStatusCode.SUCCESS, pageInfo);
    }

    @Override
    public RespBean addEmployee(Employee employee) {
        employee.CalculateContractTerm();
        employee.setSalaryId(getSalaryIdByDepartmentId(employee.getDepartmentId()));
        int insertCount = employeeMapper.insertSelective(employee);
        if (insertCount == 1) {
            Employee insertEmployee = employeeMapper.getEmployeeAllInfoById(employee.getId());
            // 发送入职欢迎邮件
            EmployeeMailDTO employeeMailDTO = new EmployeeMailDTO();
            BeanUtils.copyProperties(insertEmployee, employeeMailDTO);
            mailProducer.sendWelcomeMail(employeeMailDTO);
        }
        return insertCount == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    @Override
    // TODO 加入缓存，数据修改时更新缓存
    public Map<String, List<?>> getFixedInfo() {
        Map<String, List<?>> result = new HashMap<>();
        List<Nation> nations = nationMapper.getAllNations();
        List<PoliticsStatus> politicsStatuses = politicsStatusMapper.getAllPoliticsStatuses();
        List<JobLevel> jobLevels = jobLevelMapper.getAllJobLevels();
        List<Department> departments = departmentMapper.getAllDepartmentsByParentId(-1);
        result.put("nations", nations);
        result.put("politicsStatuses", politicsStatuses);
        result.put("jobLevels", jobLevels);
        result.put("departments", departments);
        return result;
    }

    @Override
    public List<Position> getPositions() {
        return positionMapper.getAllPositions();
    }

    @Override
    public RespBean getNextWorkId() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, String.format("%08d", employeeMapper.getMaxWorkId() + 1));
    }

    @Override
    public RespBean deleteEmployee(Integer id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setDeleted(true);
        return employeeMapper.updateByPrimaryKeySelective(employee) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }

    @Override
    public RespBean updateEmployee(Employee employee) {
        if (Objects.isNull(employee.getId())) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "id字段不能为空");
        }
        employee.CalculateContractTerm();
        return employeeMapper.updateByPrimaryKeySelective(employee) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    public List<EmployeeExportDTO> getExportEmployeesByPage(Integer startPage, Integer endPage, Integer pageSize) {
        if (endPage < startPage) {
            throw new CustomizeException("请检查导出数据开始页和结束页的范围!");
        }
        if (startPage < 1) {
            startPage = 1;
            if (endPage < startPage) {
                endPage = 1;
            }
        }
        Integer total = employeeMapper.getEmployeesTotalCount();
        int pages = (int) Math.ceil(total.doubleValue() / pageSize.doubleValue());
        if (startPage > pages) {
            startPage = pages;
        }
        Integer offset = (startPage - 1) * pageSize;
        Integer exportPageSize = (endPage - startPage + 1) * pageSize;
        return employeeMapper.getExportEmployeesByPage(null, offset, exportPageSize);
    }

    @Override
    // TODO nations、politicsStatuses、jobLevels、departments加入缓存，数据修改时更新缓存
    public Map<String, Map<String, Integer>> getAllIdMaps() {
        List<Nation> nations = nationMapper.getAllNations();
        List<PoliticsStatus> politicsStatuses = politicsStatusMapper.getAllPoliticsStatuses();
        List<Department> departments = departmentMapper.getAllDepartments();
        List<Position> positions = getPositions();
        List<JobLevel> jobLevels = jobLevelMapper.getAllJobLevels();

        Map<String, Map<String, Integer>> allIdMaps = new HashMap<>();
        Map<String, Integer> nationIdMap = nations.stream().collect(Collectors.toMap(Nation::getName, Nation::getId));
        Map<String, Integer> politicsIdMap = politicsStatuses.stream().collect(Collectors.toMap(PoliticsStatus::getName, PoliticsStatus::getId));
        Map<String, Integer> departmentIdMap = departments.stream().collect(Collectors.toMap(Department::getName, Department::getId));
        Map<String, Integer> positionIdMap = positions.stream().collect(Collectors.toMap(Position::getName, Position::getId));
        Map<String, Integer> jobLevelIdMap = jobLevels.stream().collect(Collectors.toMap(JobLevel::getName, JobLevel::getId));
        allIdMaps.put("nationIdMap", nationIdMap);
        allIdMaps.put("politicsIdMap", politicsIdMap);
        allIdMaps.put("departmentIdMap", departmentIdMap);
        allIdMaps.put("positionIdMap", positionIdMap);
        allIdMaps.put("jobLevelIdMap", jobLevelIdMap);
        return allIdMaps;
    }

    @Override
    public void saveImportEmployees(List<EmployeeImportDTO> importEmployees) {
        employeeMapper.batchInsertEmployees(importEmployees);
    }

    @Override
    public Integer getSalaryIdByDepartmentId(Integer departmentId) {
        // TODO departmentIdToSalaryIdMap 加入缓存，避免大量员工新增时反复查询，修改salary表和删除department表记录时，更新缓存
        List<Salary> salaries = salaryMapper.getAllSalaries();
        Map<Integer, Integer> departmentIdToSalaryIdMap = salaries.stream().collect(Collectors.toMap(Salary::getDepartmentId, Salary::getId));
        if (departmentIdToSalaryIdMap.containsKey(departmentId)) {
            return departmentIdToSalaryIdMap.get(departmentId);
        }
        return null;
    }

    @Override
    public RespBean getEmployeeSalaries(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> employeeSalaries = employeeMapper.getEmployeeSalaries();
        PageInfo<Employee> pageInfo = new PageInfo<>(employeeSalaries);
        return RespBean.page(CustomizeStatusCode.SUCCESS, pageInfo);
    }

    @Override
    public RespBean updateEmployeeSalary(Integer employeeId, Integer salaryId) {
        Integer currentSalaryId = employeeMapper.getSalaryIdById(employeeId);
        if (salaryId.equals(currentSalaryId)) {
            return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
        }
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setSalaryId(salaryId);
        return employeeMapper.updateByPrimaryKeySelective(employee) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }
}