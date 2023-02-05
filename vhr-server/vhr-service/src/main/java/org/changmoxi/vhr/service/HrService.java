package org.changmoxi.vhr.service;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.model.Hr;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author CZS
 * @create 2023-01-02 15:10
 **/
public interface HrService extends UserDetailsService {
    /**
     * 获取除了当前Hr之外所有Hr(操作员)数据（带有检索）
     *
     * @return
     */
    RespBean getAllOtherHrs(String keywords);

    /**
     * 更新Hr(操作员)的用户状态
     *
     * @param hr
     * @return
     */
    RespBean updateEnableStatus(Hr hr);

    /**
     * 更新用户角色
     *
     * @param hrId
     * @param rIds
     * @return
     */
    RespBean updateHrRoles(Integer hrId, Integer[] rIds);

    /**
     * 删除Hr操作员
     *
     * @param id
     * @return
     */
    RespBean deleteHr(Integer id);
}
