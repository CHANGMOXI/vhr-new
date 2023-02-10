package org.changmoxi.vhr.common.info;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author CZS
 * @create 2023-02-10 21:14
 **/
@Data
@Component
@ConfigurationProperties(prefix = "mail-producer-info")
public class MailProducerInfo {
    private String topic;
    private String tagWelcome;
}