package org.changmoxi.vhr.controller.chat;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.service.HrService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author CZS
 * @create 2023-02-15 11:31
 **/
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Resource
    private HrService hrService;

    @GetMapping("/other_hrs")
    public RespBean getAllOtherHrs() {
        return hrService.getAllOtherHrs();
    }
}