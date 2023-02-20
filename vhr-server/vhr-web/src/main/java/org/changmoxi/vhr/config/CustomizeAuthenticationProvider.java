package org.changmoxi.vhr.config;

import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.LoginException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 关于验证码校验，入门写法可以自定义过滤器(继承自GenericFilter或GenericFilterBean)加入验证码校验，并加入到 SpringSecurity 过滤器链
 * 缺点: 破坏了原有过滤器链，每次请求都要走一遍验证码校验的过滤器，较为低效不太合理，实际上只需要登录请求经过验证码校验的过滤器，其他请求不需要
 * <p>
 * 自定义认证逻辑(验证码) 优雅写法一:
 * 1.自定义 CustomizeAuthenticationProvider 继承自 DaoAuthenticationProvider，用来代替 DaoAuthenticationProvider
 * ---> 重写 {@link CustomizeAuthenticationProvider#additionalAuthenticationChecks(UserDetails, UsernamePasswordAuthenticationToken)}方法，在父类DaoAuthenticationProvider的校验逻辑基础上加入 验证码的校验
 * 2.在SecurityConfig中 重写 {@link SecurityConfig#authenticationManager()}方法，提供一个自己的 AuthenticationManager，实际上就是 ProviderManager
 * ---> 在方法中创建 ProviderManager 时，加入自己的 CustomizeAuthenticationProvider实例
 *
 * @author CZS
 * @create 2023-02-17 12:37
 * 原理:
 * 登录请求是调用 {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)}方法进行认证的，
 * 在该方法中，又会调用到 {@link DaoAuthenticationProvider#additionalAuthenticationChecks(UserDetails, UsernamePasswordAuthenticationToken)}方法做进一步的校验，主要是校验用户登录密码。
 * 因此可以自定义一个 AuthenticationProvider 代替 DaoAuthenticationProvider，重写 additionalAuthenticationChecks方法，在父类DaoAuthenticationProvider的校验逻辑基础上加入 验证码的校验。
 **/
public class CustomizeAuthenticationProvider extends DaoAuthenticationProvider {
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        //获取当前请求，也就是/doLogin登录请求
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //从当前请求中获取 code 参数，也就是用户输入的验证码
        String code = request.getParameter("code");
        //从 session 中获取服务端生成的验证码文本
        String verificationCode = (String) request.getSession().getAttribute("verification_code");
        // 验证码校验
        if (StringUtils.isBlank(code) || StringUtils.isBlank(verificationCode) || !StringUtils.equals(code.toLowerCase(), verificationCode.toLowerCase())) {
            throw new LoginException(CustomizeStatusCode.ERROR_VERIFICATION_CODE, "验证码错误!");
        }

        //验证码校验完成之后，调用父类 additionalAuthenticationChecks 方法，执行父类DaoAuthenticationProvider的校验逻辑(主要是登录密码的校验)
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}