package org.changmoxi.vhr.service.Impl;

import org.changmoxi.vhr.mapper.HrMapper;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-02 15:13
 **/
@Service
public class HrServiceImpl implements HrService {
    @Autowired
    private HrMapper hrMapper;

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
}
