package org.changmoxi.vhr.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.common.message.basic.MailProducer;
import org.changmoxi.vhr.dto.EmployeeExportDTO;
import org.changmoxi.vhr.dto.EmployeeImportDTO;
import org.changmoxi.vhr.dto.EmployeeMailDTO;
import org.changmoxi.vhr.dto.EmployeeSearchDTO;
import org.changmoxi.vhr.mapper.*;
import org.changmoxi.vhr.model.*;
import org.changmoxi.vhr.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@Slf4j
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
    @Cacheable(cacheNames = "employee:page", key = "'employees.page:' + #pageNum + '.' + #pageSize")
    public PageInfo<Employee> getEmployeesByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> employees = employeeMapper.getEmployees(null);
        PageInfo<Employee> pageInfo = new PageInfo<>(employees);
        return pageInfo;
    }

    @Override
    public PageInfo<Employee> getEmployeesByPageAndSearch(Integer pageNum, Integer pageSize, EmployeeSearchDTO employeeSearchDTO) {
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
        return pageInfo;
    }

    @Override
    public int addEmployee(Employee employee) {
        employee.CalculateContractTerm();
        int insertCount = employeeMapper.insertSelective(employee);
        if (insertCount == 1) {
            Employee insertEmployee = employeeMapper.getEmployeeAllInfoById(employee.getId());
            // 发送入职欢迎邮件
            EmployeeMailDTO employeeMailDTO = new EmployeeMailDTO();
            BeanUtils.copyProperties(insertEmployee, employeeMailDTO);
            mailProducer.sendWelcomeMail(employeeMailDTO);
        }
        return insertCount;
    }

    @Override
    @Cacheable(cacheNames = "employee", key = "'employee.fixedinfo'")
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
    @Cacheable(cacheNames = "position", key = "'all.positions'")
    public List<Position> getPositions() {
        return positionMapper.getAllPositions();
    }

    @Override
    public RespBean getNextWorkId() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, String.format("%08d", employeeMapper.getMaxWorkId() + 1));
    }

    @Override
    public int deleteEmployee(Integer id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setDeleted(true);
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public int updateEmployee(Employee employee) {
        if (Objects.isNull(employee.getId())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "id字段不能为空");
        }
        employee.CalculateContractTerm();
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public List<EmployeeExportDTO> getExportEmployeesByPage(Integer startPage, Integer endPage, Integer pageSize) {
        if (endPage < startPage) {
            throw new BusinessException("请检查导出数据开始页和结束页的范围!");
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
    @Cacheable(cacheNames = "employee", key = "'employee.all.id.maps'")
    public Map<String, Map<String, Integer>> getAllIdMaps() {
        List<Nation> nations = nationMapper.getAllNations();
        List<PoliticsStatus> politicsStatuses = politicsStatusMapper.getAllPoliticsStatuses();
        List<Department> departments = departmentMapper.getAllDepartments();
        List<Position> positions = positionMapper.getAllPositions();
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
    @Cacheable(cacheNames = "employee", key = "'employee.departmentid.to.salaryid.map'")
    public Map<Integer, Integer> getDepartmentIdToSalaryIdMap() {
        List<Salary> salaries = salaryMapper.getAllSalaries();
        return salaries.stream().collect(Collectors.toMap(Salary::getDepartmentId, Salary::getId));
    }

    @Override
    @Cacheable(cacheNames = "employee:page", key = "'employee.salaries.page:' + #pageNum + '.' + #pageSize")
    public PageInfo<Employee> getEmployeeSalaries(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> employeeSalaries = employeeMapper.getEmployeeSalaries();
        PageInfo<Employee> pageInfo = new PageInfo<>(employeeSalaries);
        return pageInfo;
    }

    @Override
    public int updateEmployeeSalary(Integer employeeId, Integer salaryId) {
        Integer currentSalaryId = employeeMapper.getSalaryIdById(employeeId);
        if (salaryId.equals(currentSalaryId)) {
            return 1;
        }
        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setSalaryId(salaryId);
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

    @Override
    public Integer getCountLessThanId(Integer id) {
        return employeeMapper.getCountLessThanId(id);
    }

    @Override
    @CacheEvict(cacheNames = "employee:page", key = "'employees.page:' + #pageNum + '.' + #pageSize")
    public void deleteEmployeesPageCache(Integer pageNum, Integer pageSize) {
    }

    @Override
    @CacheEvict(cacheNames = "employee:page", key = "'employee.salaries.page:' + #pageNum + '.' + #pageSize")
    public void deleteEmployeeSalariesPageCache(Integer pageNum, Integer pageSize) {
    }

    @Override
    @CacheEvict(cacheNames = "employee:page", allEntries = true)
    public void clearAllPageCache() {
    }
}