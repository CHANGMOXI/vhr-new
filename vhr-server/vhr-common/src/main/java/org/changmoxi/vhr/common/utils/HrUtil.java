package org.changmoxi.vhr.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.model.Hr;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author CZS
 * @create 2023-01-16 16:00
 **/
public class HrUtil {
    public static Hr getCurrentHr() {
        return ((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    public static void updateCurrentHrBasicInfo(Hr hr) {
        // 动态更新当前登录用户基本信息（不涉及用户名、密码和角色）
        Hr currentHr = getCurrentHr();
        if (StringUtils.isNotBlank(hr.getName())) {
            currentHr.setName(hr.getName());
        }
        if (StringUtils.isNotBlank(hr.getPhone())) {
            currentHr.setPhone(hr.getPhone());
        }
        if (StringUtils.isNotBlank(hr.getTelephone())) {
            currentHr.setTelephone(hr.getTelephone());
        }
        if (StringUtils.isNotBlank(hr.getAddress())) {
            currentHr.setAddress(hr.getAddress());
        }
        if (StringUtils.isNotBlank(hr.getAvatar())) {
            currentHr.setAvatar(hr.getAvatar());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 创建 新的Authentication 放到Context中，默认Authentication实现类是UsernamePasswordAuthenticationToken
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(currentHr, authentication.getCredentials(), authentication.getAuthorities()));
    }
}