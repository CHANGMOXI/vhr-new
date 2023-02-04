package org.changmoxi.vhr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author CZS
 * @create 2023-02-03 21:58
 **/
@Data
@Component
@ConfigurationProperties(prefix = "employee-fixed-info")
public class EmployeeFixedInfo {
    private String errorDataFilePath;
    private List<String> genderInfo;
    private List<String> wedlockInfo;
    private List<String> highestDegreeInfo;
    private List<String> engageFormInfo;
    private List<String> workStatusInfo;
}