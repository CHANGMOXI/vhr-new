package org.changmoxi.vhr.service.Impl;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@Slf4j
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    @Resource
    private MenuRoleMapper menuRoleMapper;

    @Override
    public RespBean getMenusByHrId() {
        Integer id = ((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return RespBean.ok(CustomizeStatusCode.SUCCESS, menuMapper.getMenusByHrId(id));
    }

    @Override
    @Cacheable(cacheNames = "menu", key = "'menus.with.roles'")
    public List<Menu> getAllMenusWithRoles() {
        return menuMapper.getAllMenusWithRoles();
    }

    @Override
    @Cacheable(cacheNames = "menu", key = "'all.enabled.menus'")
    public List<Menu> getAllMenus() {
        return menuMapper.getAllMenus();
    }

    @Override
    @Cacheable(cacheNames = "menu", key = "'enabled.mids.by.rid.' + #rId")
    public List<Integer> getEnabledMIdsByRId(Integer rId) {
        return menuRoleMapper.getEnabledMidsByRid(rId);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    @CacheEvict(cacheNames = "menu", key = "'enabled.mids.by.rid.' + #rId")
    public RespBean batchEnableMenuRoles(Integer rId, Integer[] mIds) {
        if (Objects.isNull(rId)) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "rId????????????");
        }

        List<Integer> allMIds = menuRoleMapper.getAllMIdsByRId(rId);
        if (ArrayUtils.isEmpty(mIds)) {
            if (CollectionUtils.isEmpty(allMIds)) {
                /** ??????????????????rId?????????menu_role?????????????????? **/
                return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
            } else {
                /** ??????????????????rId?????????menu_role?????????????????? **/
                return menuRoleMapper.batchEnableOrDisableMenuRoles(rId, allMIds.toArray(new Integer[0]), false) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
            }
        }
        if (CollectionUtils.isEmpty(allMIds)) {
            /** rId?????????menu_role????????????????????????????????? **/
            return menuRoleMapper.insertMenuRoles(rId, mIds) == mIds.length ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
        } else {
            Integer[] insertMIds = Arrays.stream(mIds).filter(mid -> !allMIds.contains(mid)).toArray(Integer[]::new);
            if (!ArrayUtils.isEmpty(insertMIds)) {
                if (insertMIds.length < mIds.length) {
                    /** ???????????????????????? **/
                    //????????????
                    Integer[] updateMIds = Arrays.stream(mIds).filter(allMIds::contains).toArray(Integer[]::new);
                    int updateCount = menuRoleMapper.batchEnableOrDisableMenuRoles(rId, updateMIds, true);
                    //????????????
                    int insertCount = menuRoleMapper.insertMenuRoles(rId, insertMIds);
                    if (updateCount == 0 || insertCount != insertMIds.length) {
                        //??????????????????????????????????????????
                        throw new PersistenceException("menu_role?????????????????????????????????????????????rId???" + rId + "???");
                    }
                    return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
                } else {
                    /** ???????????? **/
                    //???????????????????????????
                    int updateCount = menuRoleMapper.batchEnableOrDisableMenuRoles(rId, allMIds.toArray(new Integer[0]), false);
                    //??????
                    int insertCount = menuRoleMapper.insertMenuRoles(rId, mIds);
                    if (updateCount == 0 || insertCount != mIds.length) {
                        //??????????????????????????????????????????
                        throw new PersistenceException("menu_role????????????????????????????????????????????????????????????rId???" + rId + "???");
                    }
                    return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
                }
            }
        }
        /** mIds???menu_role???????????????????????????????????? **/
        return menuRoleMapper.batchEnableOrDisableMenuRoles(rId, mIds, true) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }
}