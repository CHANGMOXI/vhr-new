package org.changmoxi.vhr.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * 自定义过滤器(动态权限): 判断当前用户是否具备请求地址需要的角色
 *
 * @author CZS
 * @create 2023-01-05 17:28
 **/
@Component
public class UrlAccessDecisionManager implements AccessDecisionManager {
    /**
     * 判断当前用户是否具备请求地址需要的角色
     *
     * @param authentication   保存了当前登录用户的信息
     * @param object
     * @param configAttributes {@link UrlFilterInvocationSecurityMetadataSource#getAttributes(Object)} 返回的当前请求地址需要的角色集合
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        //当前用户所具有的角色
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        //遍历判断是否具有所需的其中一个角色
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute configAttribute = iterator.next();
            //当前请求地址所需的角色之一
            String needRole = configAttribute.getAttribute();

            //是否需要登录才能访问
            if (StringUtils.equals("ROLE_LOGIN", needRole)) {
                if (authentication instanceof AnonymousAuthenticationToken) {
                    //匿名用户，也就是未登录
                    throw new AccessDeniedException("尚未登录，请登录!");
                } else {
                    //已登录，放行
                    return;
                }
            }

            //判断当前用户是否具有所需的角色
            if (authorities.stream().anyMatch(a -> StringUtils.equals(a.getAuthority(), needRole))) {
                //具有所需角色，放行
                return;
            }
        }
        throw new AccessDeniedException("权限不足，请联系管理员!");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}