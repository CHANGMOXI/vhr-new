package org.changmoxi.vhr.config;

import com.alibaba.fastjson.JSON;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.exception.LoginException;
import org.changmoxi.vhr.controller.LoginController;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.service.HrService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * @author CZS
 * @create 2023-01-02 15:24
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private HrService hrService;

    @Resource
    private UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @Resource
    private UrlAccessDecisionManager urlAccessDecisionManager;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 提供一个 CustomizeAuthenticationProvider实例，创建该实例时，需要提供 UserDetailService 和 PasswordEncoder 实例
     *
     * @return
     */
    @Bean
    CustomizeAuthenticationProvider customizeAuthenticationProvider() {
        CustomizeAuthenticationProvider customizeAuthenticationProvider = new CustomizeAuthenticationProvider();
        customizeAuthenticationProvider.setUserDetailsService(hrService);
        customizeAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return customizeAuthenticationProvider;
    }

    /**
     * 重写 authenticationManager方法 提供一个自己的 AuthenticationManager，实际上就是 ProviderManager
     * 在创建 ProviderManager 时，加入自己的 CustomizeAuthenticationProvider实例
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return new ProviderManager(Arrays.asList(customizeAuthenticationProvider()));
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //未登录的非法请求会抛异常并返回登录页面，也就是要请求/login返回JSON提示未登录
        //目前问题是这个/login请求也会被拦截，然后同样未登录继续请求/login，陷入死循环
        /**
         * 第一种解决方案: 在configure(WebSecurity web)方法中放行资源，/login请求不经过Spring Security过滤器链
         */
//        web.ignoring().antMatchers("/login");
        /**
         * 第二种解决方案: 在{@link UrlFilterInvocationSecurityMetadataSource}中对/login返回null来放行
         * 第三种解决方案: 在{@link SecurityConfig#configure(HttpSecurity)}方法中加上重写的commence方法，对未登录的非法请求直接返回JSON，不进行重定向/login页面
         */
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //所有请求都需要认证后才能访问
                .anyRequest().authenticated()
                //引入自定义过滤器(动态权限)
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        //代替默认的过滤器
                        object.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
                        object.setAccessDecisionManager(urlAccessDecisionManager);
                        return object;
                    }
                })
                .and()
                //表单登录
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/doLogin")
                /**
                 * 指定登录页面，在前后端分离下后端不进行页面跳转(重定向)
                 * 在未登录去访问/login时，实际上没有返回登录页面，而是通过{@link LoginController#login()}给前端返回JSON提示信息
                 *
                 * 注意：重写commence方法不进行重定向之后，就不需要在这里指定登录页面
                 */
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
                    PrintWriter writer = response.getWriter();
                    //登录成功的Hr用户对象
                    Hr hr = (Hr) authentication.getPrincipal();
                    RespBean respBean = RespBean.ok("登录成功!", hr);
                    //写JSON字符串
                    /** Hr用户对象中的 password 不能返回给前端 **/
                    //两种办法:
                    // ①password字段 使用@JsonIgnore，序列化和返回数据到前端时都会忽略这个字段
                    // ②单次过滤，使用fastjson的SimplePropertyPreFilter过滤器，过滤指定属性
                    // ---> 如果只过滤某个层级下的指定属性，使用LevelPropertyPreFilter过滤器，根据层级过滤属性(比如xxx.xxx.password)
//                    SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
//                    filter.getExcludes().add("password");
//                    writer.write(JSON.toJSONString(respBean, filter));
                    writer.write(JSON.toJSONString(respBean));
                    writer.flush();
                    writer.close();
                })
                /** 登录失败的回调 **/
                //同样有failureHandler，该方法的参数是 AuthenticationFailureHandler对象
                //---> 实现该对象中的onAuthenticationFailure方法，三个参数: HttpServletRequest、HttpServletResponse、Exception
                //对于登录失败，会有不同的原因，Exception 则保存了登录失败的原因，可以通过 JSON 将 失败原因 返回到前端
                .failureHandler((request, response, exception) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    RespBean respBean = RespBean.error("登录失败!");
                    if (exception instanceof LoginException) {
                        respBean.setMsg(((LoginException) exception).getMsg());
                    } else if (exception instanceof LockedException) {
                        respBean.setMsg("账号被锁定，请联系管理员!");
                    } else if (exception instanceof CredentialsExpiredException) {
                        respBean.setMsg("密码过期，请联系管理员!");
                    } else if (exception instanceof AccountExpiredException) {
                        respBean.setMsg("账号过期，请联系管理员!");
                    } else if (exception instanceof DisabledException) {
                        respBean.setMsg("账号被禁用，请联系管理员!");
                    } else if (exception instanceof BadCredentialsException) {
                        respBean.setMsg("用户名或密码错误，请重新输入!");
                    }
                    //写JSON字符串
                    writer.write(JSON.toJSONString(respBean));
                    writer.flush();
                    writer.close();
                })
                .permitAll()
                .and()
                /** 注销登录的回调 **/
                //同样有logoutSuccessHandler，该方法的参数是 LogoutSuccessHandler对象
                //---> 实现该对象的onLogoutSuccess方法，三个参数: HttpServletRequest、HttpServletResponse、Authentication
                .logout()
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=utf-8");
                    PrintWriter writer = response.getWriter();
                    //写JSON字符串
                    writer.write(JSON.toJSONString(RespBean.ok("注销成功!")));
                    writer.flush();
                    writer.close();
                })
                .permitAll()
                .and()
                .csrf().disable()
                /**
                 * 未登录的非法请求，直接返回JSON提示，不要让前端进行页面跳转(重定向)
                 * AuthenticationEntryPoint接口的实现类LoginUrlAuthenticationEntryPoint重写的commence方法
                 * 默认让未登录情况下访问需要登录的请求的时候进行重定向到登录页面
                 * 所以只要重写该方法，不进行重定向，直接返回JSON提示即可
                 */
                //解决的问题: 未登录直接访问某个页面（非法请求），比如/sys/basic
                //---> 会另外请求加载菜单项的接口，被拦截到未登录，会让前端直接重定向到/localhost:8081/login
                //  ---> 此时没有经过node.js请求转发，就发生跨域问题，这样前端响应拦截器拦截的响应错误中没有response，导致页面没有任何提示信息
                //在这里的exceptionHandling().authenticationEntryPoint方法中传入AuthenticationEntryPoint对象，重写该对象的commence方法
                .exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=utf-8");

                    /** 处理服务端重启或页面长时间无操作之后，页面直接提示下面的非法请求而不是跳转登录页面的问题 **/
                    // 前端响应拦截器拦截401响应错误并跳转到登录页面
                    response.setStatus(401);

                    PrintWriter writer = response.getWriter();
                    RespBean respBean = RespBean.error("访问失败!");
                    if (authException instanceof InsufficientAuthenticationException) {
                        //未登录的非法请求
                        respBean.setMsg("未登录的非法请求，请先登录!");
                    }
                    //写JSON字符串
                    writer.write(JSON.toJSONString(respBean));
                    writer.flush();
                    writer.close();
                });
    }
}