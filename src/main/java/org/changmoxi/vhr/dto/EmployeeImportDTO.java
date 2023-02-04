package org.changmoxi.vhr.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 读取的日期数据改为用String接收，但不会检测日期是否正确
 * 在存储数据库时可能会报错，需要在解析后进行数据校验
 *
 * @author CZS
 * @create 2023-01-26 18:11
 **/
@Data
public class EmployeeImportDTO {
    @ExcelProperty("员工姓名")
    private String name;

    @ExcelProperty("工号")
    private String workId;

    @ExcelProperty("性别")
    private String gender;

    @ExcelProperty("出生日期")
    @DateTimeFormat("yyyy-MM-dd")
    private String birthday;

    @ExcelProperty("身份证号码")
    private String idCard;

    @ExcelProperty("婚姻状况")
    private String wedlock;

    @ExcelIgnore
    private Integer nationId;

    @ExcelProperty("民族")
    private String nationName;

    @ExcelProperty("籍贯")
    private String nativePlace;

    @ExcelIgnore
    private Integer politicsId;

    @ExcelProperty("政治面貌")
    private String politicsStatusName;

    @ExcelProperty("电话号码")
    private String phone;

    @ExcelProperty("电子邮箱")
    private String email;

    @ExcelProperty("联系地址")
    private String address;

    @ExcelIgnore
    private Integer departmentId;

    @ExcelProperty("所属部门")
    private String departmentName;

    @ExcelIgnore
    private Integer positionId;

    @ExcelProperty("职位")
    private String positionName;

    @ExcelIgnore
    private Integer jobLevelId;

    @ExcelProperty("职称")
    private String jobLevelName;

    @ExcelProperty("最高学历")
    private String highestDegree;

    @ExcelProperty("毕业院校")
    private String school;

    @ExcelProperty("所属专业")
    private String major;

    @ExcelProperty("聘用形式")
    private String engageForm;

    @ExcelProperty("入职日期")
    @DateTimeFormat("yyyy-MM-dd")
    private String employmentDate;

    @ExcelProperty("转正日期")
    @DateTimeFormat("yyyy-MM-dd")
    private String conversionDate;

    @ExcelProperty("合同起始日期")
    @DateTimeFormat("yyyy-MM-dd")
    private String beginContractDate;

    @ExcelProperty("合同终止日期")
    @DateTimeFormat("yyyy-MM-dd")
    private String endContractDate;

    @ExcelIgnore
    private Double contractTerm;

    @ExcelProperty("在职状态")
    private String workStatus;

    @ExcelProperty("离职日期")
    @DateTimeFormat("yyyy-MM-dd")
    private String dimissionDate;

    @ExcelIgnore
    private Integer seniority;

    @ExcelIgnore
    private Boolean deleted;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.00");

    public void CalculateContractTerm() {
        Date beginDate = null;
        Date endDate = null;
        try {
            beginDate = FORMAT.parse(this.beginContractDate);
            endDate = FORMAT.parse(this.endContractDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String beginYear = YEAR_FORMAT.format(beginDate);
        String endYear = YEAR_FORMAT.format(endDate);
        String beginMonth = MONTH_FORMAT.format(beginDate);
        String endMonth = MONTH_FORMAT.format(endDate);
        double months = (Double.parseDouble(endYear) - Double.parseDouble(beginYear)) * 12.0
                + (Double.parseDouble(endMonth) - Double.parseDouble(beginMonth));
        this.contractTerm = Double.parseDouble(DECIMAL_FORMAT.format(months / 12.0));
    }
}