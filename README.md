# vhr-new 笔记

## 前后端分离的权限管理思路
在传统的前后端不分离的开发中，权限管理主要通过<u>过滤器或者拦截器</u>来进行（权限管理框架本身也是通过过滤器来实现功能），如果用户不具备某一个角色或者某一个权限，则无法访问某一个页面。

**在前后端分离中，页面的跳转都交给前端去做，后端只提供数据，这种时候权限管理不能再按照之前的思路来做。**

- 前端是展示给用户看的，所有的菜单显示或者隐藏**目的不是为了实现权限管理，而是为了给用户一个良好的体验**，**<u>不能依靠前端隐藏控件来实现权限管理，即数据安全不能依靠前端</u>**。
- **真正的数据安全管理、权限管理是在后端实现的**，后端在接口设计的过程中，**要确保每一个接口都是在满足某种权限的基础上才能访问**。也就是说，不怕将后端数据接口地址暴露出来，即使暴露出来，只要你没有相应的角色，也是访问不了的。

总结：<u>前端所有的操作都是为了提高用户体验</u>，不能通过前端控制菜单等数据的显示或隐藏来实现权限管理，<u>这只是因为后端决定的权限导致部分菜单等数据无法访问，所以为了提供用户体验需要将用户不能访问的接口或者菜单隐藏起来</u>。比如前端请求后端菜单项数据之后动态加载菜单项，只是为了提高用户体验、前端做数据校验是为了提高效率，提高用户体验，后端需要真正的确保数据完整性。


### 加载用户菜单项数据流程
涉及的表：`hr`、`hr_role`、`role`、`menu`、`menu_role`
- 查询流程（多表联查）
  - menu m1 inner join menu m2 on m1.`id` = m2.`parentId`（一级菜单二级菜单）
    - inner join menu_role mr on mr.`mid` = m2.`id`（可操作的二级菜单）
      - inner join hr_role hrr on hrr.`rid` = mr.`rid`（用户的角色 对应 menu_role表的rid）
        - 筛选条件: m2.`enabled` = true and mr.`enabled` = true and hrr.`hrid` = 用户id


### 判断HTTP请求是否具有权限访问
前提：在menu、menu_role表中只给二级菜单分配角色，只要能访问某个二级菜单，其一级菜单在查询结果中自然会被查询到
- 流程
  - 前端发起请求
    - 分析请求url地址和menu表中哪个url匹配，进而查询相应的需要请求的二级菜单
      - 根据需要请求的二级菜单的id(menu表.`id`)，在menu_role表中查询访问该二级菜单需要的角色(menu_role表.`rid`)
        - 根据当前登录用户id在hr_role表中查询是否具有相应的角色(hr_role表.`rid`)，决定是否能访问该二级菜单接口


### 未登录情况下访问需要登录的请求 存在的问题
在自定义过滤器进行动态权限之后，未登录的非法请求会抛异常并重定向到登录页面/login
也就是要请求/login返回JSON提示未登录（后端写的/login的controller返回JSON提示未登录）
但是这个/login请求也会被拦截，然后在未登录的情况下，同样继续请求/login，陷入死循环
- 解决方案：
  - 第一种：在configure(WebSecurity web)方法中放行资源，/login请求不经过Spring Security过滤器链，但一般这种放行方式更多用于前端静态资源
    - `web.ignoring().antMatchers("/login");`
  - 第二种：在动态权限的过滤器UrlFilterInvocationSecurityMetadataSource中对/login返回null来放行
    - ```java
      if ("/login".equals(requestUrl)) {
            return null;
      }
    - 补充：AbstractSecurityInterceptor对象的rejectPublicInvocations属性默认为false，表示返回null时，放行该请求（如果需要返回null时拒绝访问，可以在SecurityConfig的configure(HttpSecurity)方法中通过withObjectPostProcessor来设置该属性为true：`object.setRejectPublicInvocations(true);`）
  - 第三种：在configure(HttpSecurity)方法中加上重写的commence方法，对未登录的非法请求直接返回JSON，不进行重定向/login页面
    - 这种方案还能解决如下问题(前两种方案无法解决)：未登录的非法请求直接访问某个页面，被拦截到未登录后，**会让前端直接重定向到/localhost:8081/login，此时没有经过node.js请求转发，就发生跨域问题，这样前端响应拦截器拦截的响应错误中没有response，导致页面没有任何提示信息**
    - ```java
      .csrf().disable()
      .exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            Result respBean = Result.error("访问失败!");
            if (authException instanceof InsufficientAuthenticationException) {
                  //未登录的非法请求
                  respBean.setMsg("未登录的非法请求，请联系管理员!");
            }
            //写JSON字符串
            out.write(JSON.toJSONString(respBean));
            out.flush();
            out.close();
      });
    - 这样配置就可以不进行重定向请求登录页面直接返回JSON提示，**避免 <u>/login请求死循环</u> 和 <u>不经过node.js请求转发而由前端直接重定向引起的跨域问题导致没有任何提示信息</u> 这些问题**


### 存在 服务端重启或页面长时间无操作之后，页面直接显示commence方法设置的非法请求提示而不是跳转登录页面 的问题
- 原因：服务端重启或页面长时间无操作之后，前端的session失效，没有登录信息，导致请求走到commence方法而直接提示非法请求
- 解决方法：在commence方法中返回JSON之前，设置http状态码为401（需要认证），前端响应拦截器拦截401响应错误之后跳转登录页面
```java
.csrf().disable()
.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
      response.setContentType("application/json;charset=utf-8");
      
      // 前端响应拦截器拦截401响应错误并跳转到登录页面
      response.setStatus(401);
      
      //参照上面的commence方法......
      });
```
```javascript
axios.interceptors.response.use(success => {
  // 对响应数据的处理，也就是http状态码在2xx范围
  // ......
}, error => {
  // 对响应错误的处理，也就是http状态码超过2xx范围
  // ......
  else if (error.response.status == 401) {
    Message.error({message: '尚未登录，请登录!'})
    // 服务端重启或页面长时间无操作之后可能会返回401响应错误，跳转到登录页面
    router.replace('/');
  }
  // ......
  
  // 返回空，没有数据，代表请求失败，方便后续判断
  return;
})
```


### MySQL表主键id重新自增排序
```sql
SET @i=0;
UPDATE `employee` SET `id` = (@i:=@i+1);
ALTER TABLE `employee` AUTO_INCREMENT = 0;
```


### 项目模块化改造
- 顶层父模块: vhr
  - 服务端模块: vhr-server
    - 服务端子模块: vhr-model
      - 实体类、DTO
    - 服务端子模块: vhr-mapper  依赖 vhr-model
      - Mapper
    - 服务端子模块: vhr-common  依赖 vhr-mapper
      - RespBean、全局异常处理、读取配置信息类、工具类、RocketMQ生产者
    - 服务端子模块: vhr-service 依赖 vhr-common
      - Service
    - 服务端子模块: vhr-web     依赖 vhr-service
      - 配置类、Controller、启动类
  - 邮件服务模块: mail-server 依赖vhr-model部分依赖，主要为了使用其中一些类