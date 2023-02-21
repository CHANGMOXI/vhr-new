package org.changmoxi.vhr.service.Impl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.common.info.FastDFSInfo;
import org.changmoxi.vhr.common.utils.FastDFSUtil;
import org.changmoxi.vhr.common.utils.HrUtil;
import org.changmoxi.vhr.mapper.HrMapper;
import org.changmoxi.vhr.mapper.HrRoleMapper;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.service.HrService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

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

    @Resource
    private FastDFSInfo fastDFSInfo;

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
    public RespBean getAllOtherHrsWithRoles(String keywords) {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, hrMapper.getAllOtherHrsWithRoles(HrUtil.getCurrentHr().getId(), keywords));
    }

    @Override
    public RespBean updateEnableStatus(Hr hr) {
        if (ObjectUtils.anyNull(hr, hr.getId())) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "hr传参不能为空 或 id、enabled字段不能为空");
        }

        Hr updateHr = new Hr();
        updateHr.setId(hr.getId());
        updateHr.setEnabled(hr.isEnabled());
        return hrMapper.updateByPrimaryKeySelective(updateHr) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    public RespBean updateHrRoles(Integer hrId, Integer[] rIds) {
        if (Objects.isNull(hrId)) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "hrId不能为空");
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
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "id不能为空");
        }
        return hrMapper.logicDelete(id) == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_DELETE) : RespBean.error(CustomizeStatusCode.ERROR_DELETE);
    }

    @Override
    public RespBean getAllOtherHrs() {
        return RespBean.ok(CustomizeStatusCode.SUCCESS, hrMapper.getAllOtherHrs(HrUtil.getCurrentHr().getId()));
    }

    @Override
    public RespBean updateHrInfo(Hr hr) {
        if (Objects.isNull(hr.getId()) || (StringUtils.isAnyBlank(hr.getName(), hr.getPhone(), hr.getTelephone(), hr.getAddress()))) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "id、name、phone、telephone、address不能为空");
        }

        // 只涉及name、phone、telephone、address的更新
        hr.setAvatar(null);
        int updateCount = hrMapper.updateBasicInfo(hr);
        if (updateCount == 1) {
            // 动态更新当前登录用户基本信息（不涉及用户名、密码和角色），不需要让用户重新登录
            HrUtil.updateCurrentHrBasicInfo(hr);
            return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE);
        }
        return RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }

    @Override
    public RespBean updateHrPassword(Integer id, String oldPassword, String newPassword) {
        if (Objects.isNull(id) || StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "id、oldPassword、newPassword不能为空");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (StringUtils.equals(oldPassword, newPassword)) {
            return RespBean.error(CustomizeStatusCode.ERROR_SAME_PASSWORD);
        }
        if (passwordEncoder.matches(oldPassword, HrUtil.getCurrentHr().getPassword())) {
            // 加密新密码
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            Hr hr = new Hr();
            hr.setId(id);
            hr.setPassword(encodedNewPassword);
            int updateCount = hrMapper.updatePassword(hr);
            return updateCount == 1 ? RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE) : RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
        }
        return RespBean.error(CustomizeStatusCode.ERROR_WRONG_OLD_PASSWORD);
    }

    @Override
    public RespBean updateHrAvatar(Integer id, MultipartFile file) {
        if (Objects.isNull(id) || file.isEmpty() || file.getSize() == 0) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "id或上传文件不能为空");
        }

        String fileId = FastDFSUtil.upload(file, null);
        if (StringUtils.isBlank(fileId)) {
            throw new BusinessException(CustomizeStatusCode.ERROR_UPLOAD, "FastDFS上传文件失败，文件名{" + file.getOriginalFilename() + "}");
        }
        String avatarUrl = fastDFSInfo.getNginxHost() + fileId;

        Hr hr = new Hr();
        hr.setId(id);
        hr.setAvatar(avatarUrl);
        int updateCount = hrMapper.updateBasicInfo(hr);
        if (updateCount == 1) {
            // TODO 加入Redis后，更新头像后要更新缓存，如果访问地址准备过期，则重新获取
            // 动态更新当前登录用户基本信息（不涉及用户名、密码和角色），不需要让用户重新登录
            HrUtil.updateCurrentHrBasicInfo(hr);
            // 返回文件访问地址
            String fileAccessUrl = FastDFSUtil.getFileAccessUrl(fileId);
            return RespBean.ok(CustomizeStatusCode.SUCCESS_UPDATE, fileAccessUrl);
        }
        return RespBean.error(CustomizeStatusCode.ERROR_UPDATE);
    }
}