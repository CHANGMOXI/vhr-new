package org.changmoxi.vhr.service;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.model.Hr;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author CZS
 * @create 2023-01-02 15:10
 **/
public interface HrService extends UserDetailsService {
    /**
     * 获取除了当前Hr之外所有Hr(操作员)数据(带有角色)（带有检索）
     *
     * @return
     */
    RespBean getAllOtherHrsWithRoles(String keywords);

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

    /**
     * 获取除了当前Hr之外所有Hr(操作员)数据(不带角色)
     *
     * @return
     */
    RespBean getAllOtherHrs();

    /**
     * 更新用户信息
     *
     * @param hr
     * @return
     */
    RespBean updateHrInfo(Hr hr);

    /**
     * 修改用户密码
     *
     * @param id
     * @param oldPassword
     * @param newPassword
     * @return
     */
    RespBean updateHrPassword(Integer id, String oldPassword, String newPassword);

    /**
     * 更新用户头像
     *
     * @param id
     * @param file
     * @return
     */
    RespBean updateHrAvatar(Integer id, MultipartFile file);
}