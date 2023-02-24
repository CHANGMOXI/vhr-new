package org.changmoxi.vhr.common.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.changmoxi.vhr.common.annotation.DoubleCache;
import org.changmoxi.vhr.common.enums.CacheType;
import org.changmoxi.vhr.common.utils.ElParser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * 自定义切面，以自定义@DoubleCache注解为切入点，操作两级缓存
 * 不足之处: 虽然已经能做到业务代码0侵入，但还不够优雅，同时自己编写的SpringEl解析器功能有限，比如暂不支持#p0以及多个#参数
 *
 * @author CZS
 * @create 2023-02-24 10:41
 **/
@Slf4j
@Aspect
@Component
public class CacheAspect {
    @Resource
    private Cache cache;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 切入点: @DoubleCache注解
     */
    @Pointcut("@annotation(org.changmoxi.vhr.common.annotation.DoubleCache)")
    public void cacheAspect() {
    }

    @Around("cacheAspect()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 拼接用于解析SpringEl表达式的map
        // 获取要增强的方法的参数名
        String[] parameterNames = signature.getParameterNames();
        // 获取要增强的方法的参数值
        Object[] args = point.getArgs();
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            treeMap.put(parameterNames[i], args[i]);
        }

        // 获取要增强的方法的@DoubleCache注解
        DoubleCache doubleCache = method.getAnnotation(DoubleCache.class);
        if (StringUtils.isAnyBlank(doubleCache.cacheName(), doubleCache.key())) {
            throw new RuntimeException("@DoubleCache注解的cacheName和key不能为空");
        }
        // 解析key中的SpringEl表达式
        String elResult = ElParser.parse(doubleCache.key(), treeMap);
        // 真正的缓存key = cacheName + ":" + key
        String cacheKey = doubleCache.cacheName() + ":" + elResult;

        /** 只存 **/
        if (doubleCache.type() == CacheType.PUT) {
            // 增强的方法执行完后的返回值写入Caffeine和Redis
            Object object = point.proceed();
            redisTemplate.opsForValue().set(cacheKey, object, doubleCache.l2CacheTimeOut(), TimeUnit.SECONDS);
            cache.put(cacheKey, object);
            log.info("更新Caffeine和Redis缓存，key={}", cacheKey);
            return object;
        }
        /** 删除 **/
        if (doubleCache.type() == CacheType.DELETE) {
            Object object = point.proceed();
            redisTemplate.delete(cacheKey);
            cache.invalidate(cacheKey);
            log.info("删除Caffeine和Redis缓存，key={}", cacheKey);
            return object;
        }
        /** 存取 **/
        // 查询Caffeine缓存
        Object caffeineCache = cache.getIfPresent(cacheKey);
        if (Objects.nonNull(caffeineCache)) {
            log.info("查询Caffeine缓存，key={}", cacheKey);
            return caffeineCache;
        }
        // 查询Redis缓存
        Object redisCache = redisTemplate.opsForValue().get(cacheKey);
        if (Objects.nonNull(redisCache)) {
            // 写入Caffeine缓存
            cache.put(cacheKey, redisCache);
            log.info("查询Redis缓存，key={}", cacheKey);
            return redisCache;
        }

        /** 两级缓存不存在 **/
        // 执行原方法(可能包含查询数据库)，获取返回值
        Object object = point.proceed();
        log.info("两级缓存不存在，执行原方法(可能包含查询数据库)，并写入两级缓存，key={}", cacheKey);
        if (Objects.nonNull(object)) {
            // 写入Redis缓存
            redisTemplate.opsForValue().set(cacheKey, object, doubleCache.l2CacheTimeOut(), TimeUnit.SECONDS);
            // 写入Caffeine缓存
            cache.put(cacheKey, object);
        }
        return object;
    }
}