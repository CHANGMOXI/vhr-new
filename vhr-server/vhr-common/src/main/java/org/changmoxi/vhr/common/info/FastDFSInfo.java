package org.changmoxi.vhr.common.info;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CZS
 * @create 2023-02-20 22:00
 **/
@Data
@Component
@ConfigurationProperties(prefix = "fastdfs")
public class FastDFSInfo {
    private String nginxHost;
    private String httpSecretKey;
}