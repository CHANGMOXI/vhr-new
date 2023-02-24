package org.changmoxi.vhr.common.annotation;

import org.changmoxi.vhr.common.enums.CacheType;

import java.lang.annotation.*;

/**
 * 自定义注解，用于操作两级缓存
 * 真正的缓存key = cacheName + ":" + key
 *
 * @author CZS
 * @create 2023-02-23 22:51
 **/
// 注解用于方法
@Target(ElementType.METHOD)
// 运行时使用
@Retention(RetentionPolicy.RUNTIME)
// 注解包含在JavaDoc中
@Documented
public @interface DoubleCache {
    /**
     * 这里的cacheName只支持一个就够了
     * 因为本项目的缓存一般就存在一个指定cacheName的Cache中，不需要做Cache隔离
     *
     * @return
     */
    String cacheName();

    /**
     * 支持springEl表达式
     *
     * @return
     */
    String key();

    long l2CacheTimeOut() default 120;

    CacheType type() default CacheType.FULL;
}