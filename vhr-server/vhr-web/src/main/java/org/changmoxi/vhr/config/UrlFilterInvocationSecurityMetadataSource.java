package org.changmoxi.vhr.config;

import org.changmoxi.vhr.model.Menu;
import org.changmoxi.vhr.model.Role;
import org.changmoxi.vhr.service.MenuService;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 自定义过滤器(动态权限): 获取当前请求地址需要的用户角色
 *
 * @author CZS
 * @create 2023-01-05 16:29
 **/
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    @Resource
    private MenuService menuService;

    /**
     * 路径匹配器，将 url路径匹配规则(/xxx/yyy/**) 与 请求地址(/xxx/yyy/zzz) 进行匹配
     */
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 获取当前请求地址需要的用户角色
     *
     * @param object 实际上是FilterInvocation对象，可以从中获取请求地址
     * @return Collection<ConfigAttribute> 当前请求地址需要的用户角色集合
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
        // /verification_code开头的请求都放行，便于前端在地址加上time参数来刷新验证码
        // 前端每次点击验证码图片，请求地址变化才会引起视图更新才能成功发起新的验证码请求
        if (antPathMatcher.match("/verification_code*", requestUrl)) {
            return null;
        }
        //未登录的非法请求的第二种解决方案:
        //---> 对返回登录页面的/login请求返回null来放行，否则未登录的请求抛异常返回登录页面会陷入/login请求死循环，因为/login也被拦截
        //这里采用第三种解决方案，直接不进行重定向/login，直接返回JSON提示，所以不需要在这里放行
//        if (StringUtils.equals("/login", requestUrl)) {
//            return null;
//        }
        List<Menu> allMenusWithRoles = menuService.getAllMenusWithRoles();
        for (Menu menu : allMenusWithRoles) {
            //每个menu的url路径匹配规则 和 当前请求地址 匹配
            if (antPathMatcher.match(menu.getUrl(), requestUrl)) {
                //匹配成功，获取当前请求地址涉及的菜单项需要的角色
                List<Role> roles = menu.getRoles();
                String[] needsRoles = roles.stream().map(Role::getName).toArray(String[]::new);
                //使用Spring Security自带方法创建一个角色集合
                return SecurityConfig.createList(needsRoles);
            }
        }
        //没有匹配上的两种处理方案:
        //①不允许访问，非法访问 ---> 如果返回null，AbstractSecurityInterceptor对象的rejectPublicInvocations属性，该属性默认为false
        //                      ---> 在SecurityConfig的configure(HttpSecurity)方法中通过withObjectPostProcessor来设置该属性为true，表示返回null时，请求不能访问
        //                   ---> 或者 返回一个临时角色，在自定义的AccessDecisionManager实现类中不给放行
        //②没有匹配上的，统一需要登录之后才能访问 ---> 返回一个临时角色，在自定义的AccessDecisionManager实现类中处理
        //这里采用第②种，所以返回"ROLE_NEED_LOGIN"的临时角色
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}