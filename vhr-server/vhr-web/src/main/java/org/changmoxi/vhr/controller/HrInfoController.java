package org.changmoxi.vhr.controller;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.utils.HrUtil;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.service.HrService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author CZS
 * @create 2023-02-19 21:41
 **/
@RestController
@RequestMapping("/hr_info")
public class HrInfoController {
    @Resource
    private HrService hrService;

    /**
     * 获取当前用户信息
     * 可以 @Resource自动注入 当前登录用户的authentication获取用户信息，也可以在 方法参数 注入，也可以使用 HrUtil
     *
     * @return
     */
    @GetMapping("/")
    public RespBean getCurrentHrInfo() {
        Hr currentHr = HrUtil.getCurrentHr();
        return RespBean.ok(CustomizeStatusCode.SUCCESS, currentHr);
    }

    /**
     * 更新用户信息
     *
     * @param hr
     * @return
     */
    @PutMapping("/")
    public RespBean updateHrInfo(@RequestBody Hr hr) {
        return hrService.updateHrInfo(hr);
    }

    /**
     * 修改用户密码
     *
     * @param passwordInfo
     * @return
     */
    @PutMapping("/password")
    public RespBean updateHrPassword(@RequestBody Map<String, Object> passwordInfo) {
        String oldPassword = (String) passwordInfo.get("oldPassword");
        String newPassword = (String) passwordInfo.get("newPassword");
        Integer id = (Integer) passwordInfo.get("hrId");
        return hrService.updateHrPassword(id, oldPassword, newPassword);
    }

    /**
     * 更新用户头像
     *
     * @param id
     * @param file
     * @return
     */
    @PostMapping("/avatar")
    public RespBean updateHrAvatar(Integer id, MultipartFile file) {
        return hrService.updateHrAvatar(id, file);
    }
}