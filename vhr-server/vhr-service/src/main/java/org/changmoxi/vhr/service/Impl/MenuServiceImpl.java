package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.mapper.MenuMapper;
import org.changmoxi.vhr.mapper.MenuRoleMapper;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.model.Menu;
import org.changmoxi.vhr.service.MenuService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-03 22:29
 **/
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    @Resource
    private MenuRoleMapper menuRoleMapper;

    @Override
//    @Cacheable//TODO 菜单项数据很少变化，每次请求都查询数据库不太合理，可以加入缓存Spring Cache 或 Redis，另外在修改角色的菜单权限之后应该在Redis中更新用户可操作的菜单项
    public RespBean getMenusByHrId() {
        Integer id = ((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return RespBean.ok(CustomizeStatusCode.SUCCESS, menuMapper.getMenusByHrId(id));
    }

    @Override
//    @Cacheable//TODO 菜单项数据很少变化，每次请求都查询数据库不太合理，可以加入缓存Spring Cache 或 Redis
    public List<Menu> getAllMenusWithRoles() {
        return menuMapper.getAllMenusWithRoles();
    }

    @Override
    public RespBean getAllMenus() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, menuMapper.getAllMenus());
    }

    @Override
    public RespBean getEnabledMIdsByRId(Integer rId) {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, menuRoleMapper.getEnabledMidsByRid(rId));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public RespBean batchEnableMenuRoles(Integer rId, Integer[] mIds) {
        if (Objects.isNull(rId)) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "rId不能为空");
        }

        List<Integer> allMIds = menuRoleMapper.getAllMIdsByRId(rId);
        if (ArrayUtils.isEmpty(mIds)) {
            if (CollectionUtils.isEmpty(allMIds)) {
                /** 全部禁用并且rId角色在menu_role表中没有记录 **/
                return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
            } else {
                /** 全部禁用并且rId角色在menu_role表中存在记录 **/
                return menuRoleMapper.batchEnableOrDisableMenuRoles(rId, allMIds.toArray(new Integer[0]), false) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
            }
        }
        if (CollectionUtils.isEmpty(allMIds)) {
            /** rId角色在menu_role表中没有记录，全部新增 **/
            return menuRoleMapper.insertMenuRoles(rId, mIds) == mIds.length ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
        } else {
            Integer[] insertMIds = Arrays.stream(mIds).filter(mid -> !allMIds.contains(mid)).toArray(Integer[]::new);
            if (!ArrayUtils.isEmpty(insertMIds)) {
                if (insertMIds.length < mIds.length) {
                    /** 部分修改部分新增 **/
                    //部分修改
                    Integer[] updateMIds = Arrays.stream(mIds).filter(allMIds::contains).toArray(Integer[]::new);
                    int updateCount = menuRoleMapper.batchEnableOrDisableMenuRoles(rId, updateMIds, true);
                    //部分新增
                    int insertCount = menuRoleMapper.insertMenuRoles(rId, insertMIds);
                    if (updateCount == 0 || insertCount != insertMIds.length) {
                        //数据操作异常，手动抛异常回滚
                        throw new PersistenceException("menu_role表修改或新增部分记录操作异常，rId【" + rId + "】");
                    }
                    return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
                } else {
                    /** 全部新增 **/
                    //已有记录修改成禁用
                    int updateCount = menuRoleMapper.batchEnableOrDisableMenuRoles(rId, allMIds.toArray(new Integer[0]), false);
                    //新增
                    int insertCount = menuRoleMapper.insertMenuRoles(rId, mIds);
                    if (updateCount == 0 || insertCount != mIds.length) {
                        //数据操作异常，手动抛异常回滚
                        throw new PersistenceException("menu_role表修改已有记录为禁用或新增记录操作异常，rId【" + rId + "】");
                    }
                    return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
                }
            }
        }
        /** mIds在menu_role表中都存在记录，全部修改 **/
        return menuRoleMapper.batchEnableOrDisableMenuRoles(rId, mIds, true) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }
}