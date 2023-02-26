package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.mapper.RoleMapper;
import org.changmoxi.vhr.model.Role;
import org.changmoxi.vhr.service.RoleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-10 11:16
 **/
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleMapper roleMapper;

    @Override
    @Cacheable(cacheNames = "role", key = "'all.roles'")
    public List<Role> getAllRoles() {
        return roleMapper.getAllRoles();
    }

    @Override
    @CacheEvict(cacheNames = "role", key = "'all.roles'")
    public RespBean addRole(Role role) {
        if (Objects.isNull(role) || StringUtils.isAnyBlank(role.getName(), role.getNameZh())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "角色role 或 name、nameZh字段不能为空");
        }

        if (!StringUtils.startsWith(role.getName(), "ROLE_")) {
            role.setName("ROLE_" + role.getName());
        }
        Role selectRole = roleMapper.getRoleByName(role.getName());
        if (Objects.nonNull(selectRole)) {
            if (StringUtils.equals(selectRole.getNameZh(), role.getNameZh())) {
                throw new BusinessException(CustomizeStatusCode.EXIST_SAME_ROLE, "已存在【" + role.getNameZh() + "】角色");
            } else {
                throw new BusinessException("已存在【" + selectRole.getName() + " : " + selectRole.getNameZh() + "】角色，添加失败!");
            }
        }
        return roleMapper.insertSelective(role) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_ADD) : RespBean.error(CustomizeStatusCode.ERROR_ADD);
    }

    @Override
    @CacheEvict(cacheNames = "role", key = "'all.roles'")
    public RespBean deleteRole(Integer rid) {
        if (Objects.isNull(rid)) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "rid不能为空");
        }
        Role role = new Role();
        role.setId(rid);
        role.setDeleted(true);
        return roleMapper.updateByPrimaryKeySelective(role) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }
}