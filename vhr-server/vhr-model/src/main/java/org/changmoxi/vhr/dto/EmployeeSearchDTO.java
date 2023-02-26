package org.changmoxi.vhr.dto;

import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

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

    public boolean isAllNull() {
        if (ObjectUtils.allNull(nationId, politicsId, departmentId, positionId, jobLevelId)
                && StringUtils.isAllBlank(name, engageForm, workStatus) && ArrayUtils.isEmpty(employmentDateScope)) {
            return true;
        }
        return false;
    }
}