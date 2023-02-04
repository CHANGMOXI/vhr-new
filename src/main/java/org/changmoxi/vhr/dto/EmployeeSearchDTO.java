package org.changmoxi.vhr.dto;

import lombok.Data;

/**
 * @author CZS
 * @create 2023-02-01 23:13
 **/
@Data
public class EmployeeSearchDTO {
    private String name;

    private Integer nationId;

    private Integer politicsId;

    private Integer departmentId;

    private Integer positionId;

    private Integer jobLevelId;

    private String engageForm;

    private String[] employmentDateScope;

    private String workStatus;
}