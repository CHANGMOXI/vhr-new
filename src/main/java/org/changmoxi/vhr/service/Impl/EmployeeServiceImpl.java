package org.changmoxi.vhr.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.exception.CustomizeException;
import org.changmoxi.vhr.mapper.*;
import org.changmoxi.vhr.model.*;
import org.changmoxi.vhr.service.EmployeeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    private static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    private static DecimalFormat decimalFormat = new DecimalFormat("##.00");

    @Override
    public RespBean getEmployeesByPage(Integer pageNum, Integer pageSize, String keywords) {
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> employees = employeeMapper.getEmployees(keywords);
        PageInfo<Employee> pageInfo = new PageInfo<>(employees);
        return RespBean.page(CustomizeStatusCode.SUCCESS, pageInfo);
    }

    @Override
    public RespBean addEmployee(Employee employee) {
        Date beginContractDate = employee.getBeginContractDate();
        Date endContractDate = employee.getEndContractDate();
        double months = (Double.parseDouble(yearFormat.format(endContractDate)) - Double.parseDouble(yearFormat.format(beginContractDate))) * 12.0 + (Double.parseDouble(monthFormat.format(endContractDate)) - Double.parseDouble(monthFormat.format(beginContractDate)));
        employee.setContractTerm(Double.parseDouble(decimalFormat.format(months / 12.0)));
        return employeeMapper.insertSelective(employee) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    @Override
    public RespBean getFixedInfo() {
        Map<String, List<?>> result = new HashMap<>();
        List<Nation> nations = nationMapper.getAllNations();
        List<PoliticsStatus> politicsStatuses = politicsStatusMapper.getAllPoliticsStatuses();
        List<JobLevel> jobLevels = jobLevelMapper.getAllJobLevels();
        List<Department> departments = departmentMapper.getAllDepartmentsByParentId(-1);
        result.put("nations", nations);
        result.put("politicsStatuses", politicsStatuses);
        result.put("jobLevels", jobLevels);
        result.put("departments", departments);
        return RespBean.ok(CustomizeStatusCode.SUCCESS, result);
    }

    @Override
    public RespBean getPositions() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, positionMapper.getAllPositions());
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
        Date beginContractDate = employee.getBeginContractDate();
        Date endContractDate = employee.getEndContractDate();
        double months = (Double.parseDouble(yearFormat.format(endContractDate)) - Double.parseDouble(yearFormat.format(beginContractDate))) * 12.0 + (Double.parseDouble(monthFormat.format(endContractDate)) - Double.parseDouble(monthFormat.format(beginContractDate)));
        employee.setContractTerm(Double.parseDouble(decimalFormat.format(months / 12.0)));
        return employeeMapper.updateByPrimaryKeySelective(employee) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }
}
