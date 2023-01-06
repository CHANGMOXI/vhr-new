package org.changmoxi.vhr.service;

import org.changmoxi.vhr.model.Menu;

import java.util.List;

/**
 * @author CZS
 * @create 2023-01-03 22:28
 **/
public interface MenuService {
    /**
     * 根据登录用户id动态获取可操作的菜单项数据
     *
     * @return
     */
    List<Menu> getMenusByHrId();

    /**
     * 获取所有带有所需用户角色的菜单项数据
     *
     * @return
     */
    List<Menu> getAllMenusWithRoles();
}
