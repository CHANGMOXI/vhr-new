package org.changmoxi.vhr.service;

import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.model.Role;

/**
 * @author CZS
 * @create 2023-01-10 11:15
 **/
public interface RoleService {
    /**
     * 获取所有角色数据
     *
     * @return
     */
    RespBean getAllRoles();

    /**
     * 添加角色
     *
     * @param role
     * @return
     */
    RespBean addRole(Role role);

    /**
     * 删除角色
     *
     * @param rid
     * @return
     */
    RespBean deleteRole(Integer rid);
}
