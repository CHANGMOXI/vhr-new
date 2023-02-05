package org.changmoxi.vhr.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.util.Date;

/**
 * @author CZS
 * @create 2023-02-01 23:47
 **/
@Data
@ColumnWidth(20)
public class EmployeeExportDTO {
    @ExcelIgnore
    private Integer id;

    @ExcelProperty(value = "员工姓名", index = 0)
    private String name;

    @ExcelProperty(value = "工号", index = 1)
    private String workId;

    @ExcelProperty(value = "性别", index = 2)
    private String gender;

    @ExcelProperty(value = "出生日期", index = 3)
    @DateTimeFormat("yyyy-MM-dd")
    private Date birthday;

    @ExcelProperty(value = "身份证号码", index = 4)
    private String idCard;

    @ExcelProperty(value = "婚姻状况", index = 5)
    private String wedlock;

    @ExcelProperty(value = "民族", index = 6)
    private String nationName;

    @ExcelProperty(value = "籍贯", index = 7)
    private String nativePlace;

    @ExcelProperty(value = "政治面貌", index = 8)
    private String politicsStatusName;

    @ExcelProperty(value = "电话号码", index = 9)
    private String phone;

    @ExcelProperty(value = "电子邮箱", index = 10)
    private String email;

    @ExcelProperty(value = "联系地址", index = 11)
    private String address;

    @ExcelProperty(value = "所属部门", index = 12)
    private String departmentName;

    @ExcelProperty(value = "职位", index = 13)
    private String positionName;

    @ExcelProperty(value = "职称", index = 14)
    private String jobLevelName;

    @ExcelProperty(value = "最高学历", index = 15)
    private String highestDegree;

    @ExcelProperty(value = "毕业院校", index = 16)
    private String school;

    @ExcelProperty(value = "所属专业", index = 17)
    private String major;

    @ExcelProperty(value = "聘用形式", index = 18)
    private String engageForm;

    @ExcelProperty(value = "入职日期", index = 19)
    @DateTimeFormat("yyyy-MM-dd")
    private Date employmentDate;

    @ExcelProperty(value = "转正日期", index = 20)
    @DateTimeFormat("yyyy-MM-dd")
    private Date conversionDate;

    @ExcelProperty(value = "合同起始日期", index = 21)
    @DateTimeFormat("yyyy-MM-dd")
    private Date beginContractDate;

    @ExcelProperty(value = "合同终止日期", index = 22)
    @DateTimeFormat("yyyy-MM-dd")
    private Date endContractDate;

    @ExcelProperty(value = "合同期限", index = 23)
    private Double contractTerm;

    @ExcelProperty(value = "在职状态", index = 24)
    private String workStatus;

    @ExcelProperty(value = "离职日期", index = 25)
    @DateTimeFormat("yyyy-MM-dd")
    private Date dimissionDate;

    @ExcelIgnore
    private Integer seniority;

    @ExcelIgnore
    private Boolean deleted;
}