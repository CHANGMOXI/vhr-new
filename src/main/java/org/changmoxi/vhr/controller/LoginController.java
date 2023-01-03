package org.changmoxi.vhr.controller;

import org.changmoxi.vhr.model.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author CZS
 * @create 2023-01-02 18:06
 **/
@RestController
public class LoginController {
    @GetMapping("/login")
    public Result login() {
        return Result.error("尚未登录，请登录!");
    }
}
