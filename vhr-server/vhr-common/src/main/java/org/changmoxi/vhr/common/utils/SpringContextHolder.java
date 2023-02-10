package org.changmoxi.vhr.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取Bean的工具类，方便在没有归入Spring管理的类中获取使用Bean
 * 工具类需要@Component来归入Spring管理
 * 当工具类作为Bean加载到Bean容器后，会调用{@link ApplicationContextAware#setApplicationContext(ApplicationContext)}方法
 * 也就是调用下面重写的{@link SpringContextHolder#setApplicationContext(ApplicationContext)}方法来注入ApplicationContext实例
 *
 * @author CZS
 * @create 2023-02-04 11:37
 **/
@Component
public class SpringContextHolder implements ApplicationContextAware {
    /**
     * 以静态变量保存ApplicationContext，方便在任何地方任何时候取出ApplicationContext
     */
    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的ApplicationContext注入方法，将其存入工具类的静态变量
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 获取静态变量存储的ApplicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 从静态变量ApplicationContext中获取Bean，自动转型对应类型
     *
     * @param requiredType
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 从静态变量ApplicationContext中获取Bean，自动转型对应类型
     *
     * @param name
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }
}