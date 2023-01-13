package org.changmoxi.vhr.service;

import org.changmoxi.vhr.model.Menu;
import org.changmoxi.vhr.model.RespBean;

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
    RespBean getMenusByHrId();

    /**
     * 获取所有带有所需用户角色的菜单项数据
     *
     * @return
     */
    List<Menu> getAllMenusWithRoles();

    /**
     * 获取已启用的所有菜单项数据
     *
     * @return
     */
    RespBean getAllMenus();

    /**
     * 获取角色可操作的菜单项id
     *
     * @param rid
     * @return
     */
    RespBean getEnabledMidsByRid(Integer rid);

    /**
     * 更新角色可操作的菜单项id
     *
     * @param rid
     * @param mids
     * @return
     */
    RespBean batchEnableMenuRoles(Integer rid, Integer[] mids);
}
