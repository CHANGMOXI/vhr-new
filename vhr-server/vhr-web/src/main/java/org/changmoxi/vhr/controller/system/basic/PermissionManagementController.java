package org.changmoxi.vhr.controller.system.basic;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.model.Role;
import org.changmoxi.vhr.service.MenuService;
import org.changmoxi.vhr.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author CZS
 * @create 2023-01-10 9:25
 **/
@RestController
@RequestMapping("/system/basic/permissions")
public class PermissionManagementController {
    @Resource
    private RoleService roleService;

    @Resource
    private MenuService menuService;

    /**
     * 获取所有角色数据
     *
     * @return
     */
    @GetMapping("/roles")
    public RespBean getAllRoles() {
        return roleService.getAllRoles();
    }

    /**
     * 获取已启用的所有菜单项数据
     *
     * @return
     */
    @GetMapping("/menus")
    public RespBean getAllMenus() {
        return menuService.getAllMenus();
    }

    /**
     * 获取角色可操作的菜单项id
     *
     * @param rId
     * @return
     */
    @GetMapping("/mids/{rid}")
    public RespBean getEnabledMIdsByRId(@PathVariable("rid") Integer rId) {
        return menuService.getEnabledMIdsByRId(rId);
    }

    /**
     * 更新角色可操作的菜单项id
     *
     * @param rId
     * @param mIds
     * @return
     */
    @PutMapping("/")
    public RespBean batchEnableMenuRoles(Integer rId, Integer[] mIds) {
        return menuService.batchEnableMenuRoles(rId, mIds);
    }

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    @PostMapping("/role")
    public RespBean addRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    /**
     * 删除角色
     *
     * @param rId
     * @return
     */
    @DeleteMapping("/role/{rId}")
    public RespBean deleteRole(@PathVariable Integer rId) {
        return roleService.deleteRole(rId);
    }
}
