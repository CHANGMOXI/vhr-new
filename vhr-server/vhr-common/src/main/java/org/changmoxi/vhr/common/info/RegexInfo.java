package org.changmoxi.vhr.common.info;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CZS
 * @create 2023-02-03 21:58
 **/
@Data
@Component
@ConfigurationProperties(prefix = "regex-info")
public class RegexInfo {
    private String dateRegex;
    private String idCardRegex;
    private String phoneRegex;
    private String emailRegex;
}