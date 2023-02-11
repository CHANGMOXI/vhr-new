package org.changmoxi.vhr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Employee {
    private Integer id;

    @NotBlank(message = "员工姓名不能为空")
    private String name;

    @NotBlank(message = "工号不能为空")
    private String workId;

    @NotBlank(message = "性别不能为空")
    private String gender;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "出生日期不能为空")
    private Date birthday;

    @NotBlank(message = "身份证号码不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号码格式不正确")
    private String idCard;

    @NotBlank(message = "婚姻状况不能为空")
    private String wedlock;

    @NotNull(message = "民族不能为空")
    private Integer nationId;

    /**
     * 扩展字段
     */
    private String nationName;

    @NotBlank(message = "籍贯不能为空")
    private String nativePlace;

    @NotNull(message = "政治面貌不能为空")
    private Integer politicsId;

    /**
     * 扩展字段
     */
    private String politicsStatusName;

    @NotBlank(message = "电话号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "电话号码格式不正确")
    private String phone;

    @NotBlank(message = "电子邮箱不能为空")
    @Pattern(regexp = "^[a-zA-Z1-9]([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@[a-zA-Z1-9]([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*[\\.][a-zA-Z]{2,3}([\\.][a-zA-Z]{2,3})*$", message = "电子邮箱格式不正确")
    private String email;

    @NotBlank(message = "联系地址不能为空")
    private String address;

    @NotNull(message = "所属部门不能为空")
    private Integer departmentId;

    /**
     * 扩展字段
     */
    private String departmentName;

    @NotNull(message = "职位不能为空")
    private Integer positionId;

    /**
     * 扩展字段
     */
    private String positionName;

    @NotNull(message = "职称不能为空")
    private Integer jobLevelId;

    /**
     * 扩展字段
     */
    private String jobLevelName;

    @NotBlank(message = "最高学历不能为空")
    private String highestDegree;

    @NotBlank(message = "毕业院校不能为空")
    private String school;

    @NotBlank(message = "所属专业不能为空")
    private String major;

    @NotBlank(message = "聘用形式不能为空")
    private String engageForm;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "入职日期不能为空")
    private Date employmentDate;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "转正日期不能为空")
    private Date conversionDate;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "合同起始日期不能为空")
    private Date beginContractDate;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "合同终止日期不能为空")
    private Date endContractDate;

    private Double contractTerm;

    @NotBlank(message = "在职状态不能为空")
    private String workStatus;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date dimissionDate;

    private Integer seniority;

    private Boolean deleted;

    private static final SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.00");

    public void CalculateContractTerm() {
        double months = (Double.parseDouble(YEAR_FORMAT.format(this.endContractDate)) - Double.parseDouble(YEAR_FORMAT.format(this.beginContractDate))) * 12.0
                + (Double.parseDouble(MONTH_FORMAT.format(this.endContractDate)) - Double.parseDouble(MONTH_FORMAT.format(this.beginContractDate)));
        this.contractTerm = Double.parseDouble(DECIMAL_FORMAT.format(months / 12.0));
    }
}