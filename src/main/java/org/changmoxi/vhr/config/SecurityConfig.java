package org.changmoxi.vhr.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import org.changmoxi.vhr.controller.LoginController;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.model.Result;
import org.changmoxi.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.PrintWriter;

/**
 * @author CZS
 * @create 2023-01-02 15:24
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private HrService hrService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //所有请求都需认证后才能访问
                .anyRequest().authenticated()
                .and()
                //表单登录
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/doLogin")
                /** 指定登录页面，在前后端分离下后端不进行页面跳转，而是在未登录去访问/login时，通过{@link LoginController#login()}给前端返回提示信息 **/
                .loginPage("/login")
                /** 登录成功的回调 **/
                //successHandler功能强大，还包括了defaultSuccessUrl 和 successForwardUrl 的功能
                //后面两个适合前后端不分离的登录成功跳转
                //successHandler方法的参数是 AuthenticationSuccessHandler对象
                //---> 要实现该对象中的onAuthenticationSuccess方法，该方法有三个参数：HttpServletRequest、HttpServletResponse、Authentication
                //  ---> 可以进行各种方式的返回数据，比如HttpServletRequest做服务端跳转，HttpServletResponse做客户端跳转，同时也可以返回JSON
                //  ---> 第三个参数 Authentication 则保存了刚刚登录成功的用户信息
                .successHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    //登录成功的Hr用户对象
                    Hr hr = (Hr) authentication.getPrincipal();
                    Result result = Result.success("登录成功!", hr);
                    //写JSON字符串
                    /** Hr用户对象中的 password 不能返回给前端 **/
                    //使用fastjson的SimplePropertyPreFilter过滤器，过滤指定属性
                    //如果只过滤某个层级下的指定属性，使用LevelPropertyPreFilter过滤器，根据层级过滤属性(比如xxx.xxx.password)
                    SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
                    filter.getExcludes().add("password");
                    out.write(JSON.toJSONString(result, filter));
                    out.flush();
                    out.close();
                })
                /** 登录失败的回调 **/
                //同样有failureHandler，该方法的参数是 AuthenticationFailureHandler对象
                //---> 实现该对象中的onAuthenticationFailure方法，三个参数: HttpServletRequest、HttpServletResponse、Exception
                //对于登录失败，会有不同的原因，Exception 则保存了登录失败的原因，可以通过 JSON 将 失败原因 返回到前端
                .failureHandler((request, response, exception) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    Result result = Result.error("登录失败!");
                    if (exception instanceof LockedException) {
                        result.setMsg("账号被锁定，请联系管理员!");
                    } else if (exception instanceof CredentialsExpiredException) {
                        result.setMsg("密码过期，请联系管理员!");
                    } else if (exception instanceof AccountExpiredException) {
                        result.setMsg("账号过期，请联系管理员!");
                    } else if (exception instanceof DisabledException) {
                        result.setMsg("账号被禁用，请联系管理员!");
                    } else if (exception instanceof BadCredentialsException) {
                        result.setMsg("用户名或密码错误，请重新输入!");
                    }
                    //写JSON字符串
                    out.write(JSON.toJSONString(result));
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                /** 注销登录的回调 **/
                //同样有logoutSuccessHandler，该方法的参数是 LogoutSuccessHandler对象
                //---> 实现该对象的onLogoutSuccess方法，三个参数: HttpServletRequest、HttpServletResponse、Authentication
                .logout()
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter out = response.getWriter();
                    //写JSON字符串
                    out.write(JSON.toJSONString(Result.success("注销成功!")));
                    out.flush();
                    out.close();
                })
                .permitAll()
                .and()
                .csrf().disable();
    }
}
