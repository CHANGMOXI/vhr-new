package org.changmoxi.vhr.controller.config;

import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.model.Menu;
import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.service.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CZS
 * @create 2023-01-03 22:24
 **/
@RestController
@RequestMapping("/system/config")
public class SystemConfigController {
    @Resource
    private MenuService menuService;

    /**
     * 通过登录用户id动态获取可操作的菜单项数据
     * 注意：不能用前端传递的id，不安全
     *
     * @return
     */
    @GetMapping("/menus")
    public RespBean getMenusByHrId() {
        List<Menu> menuList = menuService.getMenusByHrId();
        return RespBean.ok(CustomizeStatusCode.SUCCESS, menuList);
    }
}
