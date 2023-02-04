package org.changmoxi.vhr.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.context.AnalysisContext;
import lombok.Data;

/**
 * 错误员工数据收集表
 * 导出的列都用String写出
 *
 * @author CZS
 * @create 2023-02-02 21:30
 **/
@Data
@ColumnWidth(20)
public class EmployeeErrorDTO {
    /**
     * 错误提示信息: 错误数据行
     */
    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "错误数据行"}, index = 0)
    private Integer errorDataRow;

    /**
     * 错误提示信息: 备注
     */
    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "备注"}, index = 1)
    private String remark;

    @ExcelIgnore
    private Integer id;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "员工姓名"}, index = 2)
    private String name;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "工号"}, index = 3)
    private String workId;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "性别"}, index = 4)
    private String gender;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "出生日期"}, index = 5)
    private String birthday;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "身份证号码"}, index = 6)
    private String idCard;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "婚姻状况"}, index = 7)
    private String wedlock;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "民族"}, index = 8)
    private String nationName;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "籍贯"}, index = 9)
    private String nativePlace;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "政治面貌"}, index = 10)
    private String politicsStatusName;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "电话号码"}, index = 11)
    private String phone;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "电子邮箱"}, index = 12)
    private String email;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "联系地址"}, index = 13)
    private String address;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "所属部门"}, index = 14)
    private String departmentName;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "职位"}, index = 15)
    private String positionName;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "职称"}, index = 16)
    private String jobLevelName;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "最高学历"}, index = 17)
    private String highestDegree;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "毕业院校"}, index = 18)
    private String school;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "所属专业"}, index = 19)
    private String major;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "聘用形式"}, index = 20)
    private String engageForm;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "入职日期"}, index = 21)
    private String employmentDate;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "转正日期"}, index = 22)
    private String conversionDate;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "合同起始日期"}, index = 23)
    private String beginContractDate;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "合同终止日期"}, index = 24)
    private String endContractDate;

    @ExcelIgnore
    private String contractTerm;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "在职状态"}, index = 25)
    private String workStatus;

    @ExcelProperty(value = {"转换异常的错误只展示该行的第一个转换异常内容，请检查该行是否存在其他转换异常内容", "离职日期"}, index = 26)
    private String dimissionDate;

    @ExcelIgnore
    private Integer seniority;

    @ExcelIgnore
    private Boolean deleted;

    /**
     * 设置错误提示，若新增错误提示信息，则相应地扩展该方法
     *
     * @param context
     * @param remark
     */
    public void setErrorTips(AnalysisContext context, String remark) {
        // 错误数据行，getRowIndex()返回的是当前的行数，从0开始，与Excel从1开始不一样
        this.errorDataRow = context.readRowHolder().getRowIndex() + 1;
        // 备注，比如数据转换异常提示
        this.remark = remark;
    }
}