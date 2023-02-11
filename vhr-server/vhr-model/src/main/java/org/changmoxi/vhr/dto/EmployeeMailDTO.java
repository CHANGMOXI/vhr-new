package org.changmoxi.vhr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author CZS
 * @create 2023-02-11 14:17
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMailDTO implements Serializable {
    private Integer id;

    private String name;

    private String workId;

    private String gender;

    private String birthday;

    private String idCard;

    private String wedlock;

    private Integer nationId;

    private String nationName;

    private String nativePlace;

    private Integer politicsId;

    private String politicsStatusName;

    private String phone;

    private String email;

    private String address;

    private Integer departmentId;

    private String departmentName;

    private Integer positionId;

    private String positionName;

    private Integer jobLevelId;

    private String jobLevelName;

    private String highestDegree;

    private String school;

    private String major;

    private String engageForm;

    private String employmentDate;

    private String conversionDate;

    private String beginContractDate;

    private String endContractDate;

    private Double contractTerm;

    private String workStatus;

    private String dimissionDate;

    private Integer seniority;

    private Boolean deleted;
}