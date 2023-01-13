package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.exception.CustomizeException;
import org.changmoxi.vhr.mapper.MenuMapper;
import org.changmoxi.vhr.mapper.MenuRoleMapper;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.model.Menu;
import org.changmoxi.vhr.model.RespBean;
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
    public RespBean getEnabledMidsByRid(Integer rid) {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, menuRoleMapper.getEnabledMidsByRid(rid));
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public RespBean batchEnableMenuRoles(Integer rid, Integer[] mids) {
        if (Objects.isNull(rid)) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "rid不能为空");
        }

        List<Integer> allMids = menuRoleMapper.getAllMidsByRid(rid);
        if (ArrayUtils.isEmpty(mids)) {
            if (CollectionUtils.isEmpty(allMids)) {
                /** 全部禁用并且rid角色在menu_role表中没有记录 **/
                return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
            } else {
                /** 全部禁用并且rid角色在menu_role表中存在记录 **/
                return menuRoleMapper.batchEnableOrDisableMenuRoles(rid, allMids.toArray(new Integer[0]), false) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
            }
        }
        if (CollectionUtils.isEmpty(allMids)) {
            /** rid角色在menu_role表中没有记录，全部新增 **/
            return menuRoleMapper.insertMenuRoles(rid, mids) == mids.length ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
        } else {
            Integer[] insertMids = Arrays.stream(mids).filter(mid -> !allMids.contains(mid)).toArray(Integer[]::new);
            if (!ArrayUtils.isEmpty(insertMids)) {
                if (insertMids.length < mids.length) {
                    /** 部分修改部分新增 **/
                    //部分修改
                    Integer[] updateMids = Arrays.stream(mids).filter(allMids::contains).toArray(Integer[]::new);
                    int updateCount = menuRoleMapper.batchEnableOrDisableMenuRoles(rid, updateMids, true);
                    //部分新增
                    int insertCount = menuRoleMapper.insertMenuRoles(rid, insertMids);
                    if (updateCount == 0 || insertCount != insertMids.length) {
                        //数据操作异常，手动抛异常回滚
                        throw new PersistenceException("menu_role表修改或新增部分记录操作异常，rid【" + rid + "】");
                    }
                    return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
                } else {
                    /** 全部新增 **/
                    //已有记录修改成禁用
                    int updateCount = menuRoleMapper.batchEnableOrDisableMenuRoles(rid, allMids.toArray(new Integer[0]), false);
                    //新增
                    int insertCount = menuRoleMapper.insertMenuRoles(rid, mids);
                    if (updateCount == 0 || insertCount != mids.length) {
                        //数据操作异常，手动抛异常回滚
                        throw new PersistenceException("menu_role表修改已有记录为禁用或新增记录操作异常，rid【" + rid + "】");
                    }
                    return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
                }
            }
        }
        /** mids在menu_role表中都存在记录，全部修改 **/
        return menuRoleMapper.batchEnableOrDisableMenuRoles(rid, mids, true) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }
}
