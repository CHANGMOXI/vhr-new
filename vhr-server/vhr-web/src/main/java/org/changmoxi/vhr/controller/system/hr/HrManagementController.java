package org.changmoxi.vhr.controller.system.hr;

import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.service.HrService;
import org.changmoxi.vhr.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author CZS
 * @create 2023-01-16 15:13
 **/
@RestController
@RequestMapping("/system/hr")
public class HrManagementController {
    @Resource
    private HrService hrService;

    @Resource
    private RoleService roleService;

    /**
     * 获取除了当前Hr操作员之外所有Hr操作员数据(带有角色)（带有检索）
     *
     * @return
     */
    @GetMapping("/")
    public RespBean getAllOtherHrs(String keywords) {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, StringUtils.isBlank(keywords) ? hrService.getAllOtherHrsWithRoles() : hrService.getAllOtherHrsWithRolesBySearch(keywords));
    }

    /**
     * 更新Hr操作员的用户状态
     *
     * @param hr
     * @return
     */
    @PutMapping("/status")
    public RespBean updateEnableStatus(@RequestBody Hr hr) {
        return hrService.updateEnableStatus(hr);
    }

    /**
     * 获取所有角色数据
     *
     * @return
     */
    @GetMapping("/roles")
    public RespBean getAllRoles() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, roleService.getAllRoles());
    }

    /**
     * 更新Hr操作员的角色
     *
     * @param hrId
     * @param rIds
     * @return
     */
    @PutMapping("/roles")
    public RespBean updateHrRoles(Integer hrId, Integer[] rIds) {
        return hrService.updateHrRoles(hrId, rIds);
    }

    /**
     * 删除Hr操作员
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public RespBean deleteHr(@PathVariable Integer id) {
        return hrService.deleteHr(id);
    }
}