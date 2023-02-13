package org.changmoxi.vhr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
public class Salary {
    private Integer id;

    @NotBlank(message = "工资账套名称不能为空")
    @Pattern(regexp = "^\\S+(.*\\S+)*$", message = "工资账套名称不能为空且不能以空格开始或结束")
    private String name;

    @NotNull(message = "部门id不能为空")
    @Min(value = 1, message = "部门id必须是整数，不能小于1")
    @Digits(integer = 8, fraction = 0, message = "部门id必须是整数，上限8位")
    private Integer departmentId;

    /**
     * 扩展字段
     */
    private String departmentName;

    @NotNull(message = "基本工资不能为空")
    @Min(value = 1, message = "基本工资必须是整数，不能小于1")
    @Digits(integer = 8, fraction = 0, message = "基本工资必须是整数，上限8位")
    private Integer basicSalary;

    @NotNull(message = "奖金不能为空")
    @Min(value = 1, message = "奖金必须是整数，不能小于1")
    @Digits(integer = 8, fraction = 0, message = "奖金必须是整数，上限8位")
    private Integer bonus;

    @NotNull(message = "午餐补助不能为空")
    @Min(value = 1, message = "午餐补助必须是整数，不能小于1")
    @Digits(integer = 8, fraction = 0, message = "午餐补助必须是整数，上限8位")
    private Integer lunchAllowance;

    @NotNull(message = "交通补助不能为空")
    @Min(value = 1, message = "交通补助必须是整数，不能小于1")
    @Digits(integer = 8, fraction = 0, message = "交通补助必须是整数，上限8位")
    private Integer transportationAllowance;

    private Integer payableSalary;

    @NotNull(message = "养老金基数不能为空")
    @Min(value = 1, message = "养老金基数必须是整数，不能小于1")
    @Digits(integer = 8, fraction = 0, message = "养老金基数必须是整数，上限8位")
    private Integer pensionBase;

    @NotNull(message = "养老金比例不能为空")
    @DecimalMax(value = "1", message = "养老金比例必须是不大于1的小数")
    @DecimalMin(value = "0.01", message = "养老金比例必须是不小于0.01的小数")
    @Digits(integer = 1, fraction = 3, message = "养老金比例必须是不小于0.01且不大于1的小数，小数位上限3位")
    private Double pensionRatio;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date enableDate;

    @NotNull(message = "医疗保险基数不能为空")
    @Min(value = 1, message = "医疗保险基数必须是整数，不能小于1")
    @Digits(integer = 8, fraction = 0, message = "医疗保险基数必须是整数，上限8位")
    private Integer medicalBase;

    @NotNull(message = "医疗保险比例不能为空")
    @DecimalMax(value = "1", message = "医疗保险比例必须是不大于1的小数")
    @DecimalMin(value = "0.01", message = "医疗保险比例必须是不小于0.01的小数")
    @Digits(integer = 1, fraction = 3, message = "医疗保险比例必须是不小于0.01且不大于1的小数，小数位上限3位")
    private Double medicalRatio;

    @NotNull(message = "公积金基数不能为空")
    @Min(value = 1, message = "公积金基数必须是整数，不能小于1")
    @Digits(integer = 8, fraction = 0, message = "公积金基数必须是整数，上限8位")
    private Integer accumulationFundBase;

    @NotNull(message = "公积金比例不能为空")
    @DecimalMax(value = "1", message = "公积金比例必须是不大于1的小数")
    @DecimalMin(value = "0.01", message = "公积金比例必须是不小于0.01的小数")
    @Digits(integer = 1, fraction = 3, message = "公积金比例必须是不小于0.01且不大于1的小数，小数位上限3位")
    private Double accumulationFundRatio;

    private Boolean deleted;
}