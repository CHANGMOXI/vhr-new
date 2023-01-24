package org.changmoxi.vhr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class Employee {
    private Integer id;

    @NotBlank(message = "员工姓名不能为空")
    private String name;

    @NotBlank(message = "性别不能为空")
    private String gender;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "出生日期不能为空")
    private Date birthday;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @NotBlank(message = "婚姻状况不能为空")
    private String wedlock;

    @NotNull(message = "民族不能为空")
    private Integer nationId;

    @NotBlank(message = "籍贯不能为空")
    private String nativePlace;

    @NotNull(message = "政治面貌不能为空")
    private Integer politicsId;

    @Email(message = "电子邮箱格式错误")
    @NotBlank(message = "电子邮箱不能为空")
    private String email;

    @NotBlank(message = "电话号码不能为空")
    private String phone;

    @NotBlank(message = "联系地址不能为空")
    private String address;

    @NotNull(message = "所属部门不能为空")
    private Integer departmentId;

    @NotNull(message = "职称不能为空")
    private Integer jobLevelId;

    @NotNull(message = "职位不能为空")
    private Integer positionId;

    @NotBlank(message = "聘用形式不能为空")
    private String engageForm;

    @NotBlank(message = "最高学历不能为空")
    private String highestDegree;

    @NotBlank(message = "所属专业不能为空")
    private String major;

    @NotBlank(message = "毕业院校不能为空")
    private String school;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @NotNull(message = "入职日期不能为空")
    private Date employmentDate;

    @NotBlank(message = "在职状态不能为空")
    private String workStatus;

    @NotBlank(message = "工号不能为空")
    private String workId;

    private Double contractTerm;

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
    private Date dimissionDate;

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

    private Integer seniority;

    private Boolean deleted;

    private Nation nation;

    private PoliticsStatus politicsStatus;

    private Department department;

    private JobLevel jobLevel;

    private Position position;
}