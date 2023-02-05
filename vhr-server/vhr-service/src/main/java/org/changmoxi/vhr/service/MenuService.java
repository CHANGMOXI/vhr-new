package org.changmoxi.vhr.service;

import org.changmoxi.vhr.common.RespBean;
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
     * @param rId
     * @return
     */
    RespBean getEnabledMIdsByRId(Integer rId);

    /**
     * 更新角色可操作的菜单项id
     *
     * @param rId
     * @param mIds
     * @return
     */
    RespBean batchEnableMenuRoles(Integer rId, Integer[] mIds);
}
