package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.CustomizeException;
import org.changmoxi.vhr.mapper.HrMapper;
import org.changmoxi.vhr.mapper.HrRoleMapper;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.service.HrService;
import org.changmoxi.vhr.common.utils.HrUtil;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-02 15:13
 **/
@Service
public class HrServiceImpl implements HrService {
    @Resource
    private HrMapper hrMapper;

    @Resource
    private HrRoleMapper hrRoleMapper;

    /**
     * Spring Security 根据 username 查询返回 Hr用户
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Hr hr = hrMapper.selectByUsername(username);
        if (Objects.isNull(hr)) {
            throw new UsernameNotFoundException("用户名不存在!");
        }
        //用户存在，设置其具备的角色
        hr.setRoles(hrMapper.getHrRolesById(hr.getId()));
        return hr;
    }

    @Override
    public RespBean getAllOtherHrs(String keywords) {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, hrMapper.getAllOtherHrs(HrUtil.getCurrentHr().getId(), keywords));
    }

    @Override
    public RespBean updateEnableStatus(Hr hr) {
        if (Objects.isNull(hr) || Objects.isNull(hr.getId())) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "hr传参不能为空 或 id、enabled字段不能为空");
        }

        Hr updateHr = new Hr();
        updateHr.setId(hr.getId());
        updateHr.setEnabled(hr.isEnabled());
        return hrMapper.updateByPrimaryKeySelective(updateHr) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    public RespBean updateHrRoles(Integer hrId, Integer[] rIds) {
        if (Objects.isNull(hrId)) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "hrId不能为空");
        }

        List<Integer> allRIds = hrRoleMapper.getAllRIdsByHrId(hrId);
        if (ArrayUtils.isEmpty(rIds)) {
            if (CollectionUtils.isEmpty(allRIds)) {
                /** 全部禁用并且hrId在hr_role表中没有记录 **/
                return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
            } else {
                /** 全部禁用并且hrId在hr_role表中存在记录 **/
                return hrRoleMapper.batchEnableOrDisableHrRoles(hrId, allRIds.toArray(new Integer[0]), false) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
            }
        }
        if (CollectionUtils.isEmpty(allRIds)) {
            /** hrId在hr_role表中没有记录，全部新增 **/
            return hrRoleMapper.insertHrRoles(hrId, rIds) == rIds.length ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
        } else {
            Integer[] insertRIds = Arrays.stream(rIds).filter(mid -> !allRIds.contains(mid)).toArray(Integer[]::new);
            if (!ArrayUtils.isEmpty(insertRIds)) {
                if (insertRIds.length < rIds.length) {
                    /** 部分修改部分新增 **/
                    //部分修改
                    Integer[] updateRIds = Arrays.stream(rIds).filter(allRIds::contains).toArray(Integer[]::new);
                    int updateCount = hrRoleMapper.batchEnableOrDisableHrRoles(hrId, updateRIds, true);
                    //部分新增
                    int insertCount = hrRoleMapper.insertHrRoles(hrId, insertRIds);
                    if (updateCount == 0 || insertCount != insertRIds.length) {
                        //数据操作异常，手动抛异常回滚
                        throw new PersistenceException("hr_role表修改或新增部分记录操作异常，hrId【" + hrId + "】");
                    }
                    return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
                } else {
                    /** 全部新增 **/
                    //已有记录修改成禁用
                    int updateCount = hrRoleMapper.batchEnableOrDisableHrRoles(hrId, allRIds.toArray(new Integer[0]), false);
                    //新增
                    int insertCount = hrRoleMapper.insertHrRoles(hrId, rIds);
                    if (updateCount == 0 || insertCount != rIds.length) {
                        //数据操作异常，手动抛异常回滚
                        throw new PersistenceException("hr_role表修改已有记录为禁用或新增记录操作异常，hrId【" + hrId + "】");
                    }
                    return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
                }
            }
        }
        /** rIds在hr_role表中都存在记录，全部修改 **/
        return hrRoleMapper.batchEnableOrDisableHrRoles(hrId, rIds, true) > 0 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    public RespBean deleteHr(Integer id) {
        if (Objects.isNull(id)) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "id不能为空");
        }
        return hrMapper.logicDelete(id) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }
}