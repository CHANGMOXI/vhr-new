package org.changmoxi.vhr.common.info;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CZS
 * @create 2023-02-24 15:27
 **/
@Data
@Component
@ConfigurationProperties(prefix = "doublecache")
public class DoubleCacheConfigInfo {
    private Boolean allowNullValues = true;
    private Integer initialCapacity = 128;
    private Integer maximumSize = 1024;
    private Long expireAfterWrite;
    private Long expireAfterAccess;
    private Long refreshAfterWrite;
    private Long redisExpire;
    private Map<String, Map<String, Long>> customizeRedisExpire;
    private Map<String, Long> allCustomizeRedisExpire = new HashMap<>();

    /**
     * 初始化allCustomizeRedisExpire
     */
    @PostConstruct
    private void init() {
        for (Map.Entry<String, Map<String, Long>> entry : customizeRedisExpire.entrySet()) {
            String cacheName = entry.getKey();
            entry.getValue().forEach((key, value) -> {
                String redisKey = cacheName + ":" + key;
                allCustomizeRedisExpire.put(redisKey, value);
            });
        }
    }
}