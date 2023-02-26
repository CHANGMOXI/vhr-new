package org.changmoxi.vhr.config;

import org.changmoxi.vhr.common.cache.DoubleCacheManager;
import org.changmoxi.vhr.common.info.DoubleCacheConfigInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author CZS
 * @create 2023-02-24 15:31
 **/
@Configuration
public class CacheConfig {
    /**
     * 配置自定义的DoubleCacheManager作为默认的缓存管理器
     * 被@Configuration标注的类也是Spring组件，可以直接通过方法形参注入其他组件(比如这里的redisTemplate和自定义缓存配置信息doubleCacheConfigInfo)
     *
     * @param redisTemplate
     * @param doubleCacheConfigInfo
     * @return
     */
    @Bean
    public DoubleCacheManager cacheManager(RedisTemplate<String, Object> redisTemplate, DoubleCacheConfigInfo doubleCacheConfigInfo) {
        return new DoubleCacheManager(redisTemplate, doubleCacheConfigInfo);
    }
}