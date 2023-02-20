package org.changmoxi.vhr.common.exception;

import lombok.Getter;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义登录异常类
 *
 * @author CZS
 * @create 2023-02-17 13:13
 **/
@Getter
public class LoginException extends AuthenticationException {
    private Integer code;
    private String msg;

    /**
     * 手动设置异常
     *
     * @param statusCode
     * @param message
     */
    public LoginException(StatusCode statusCode, String message) {
        //手动设置异常时设置的message，异常错误的详情
        super(message);
        //状态码
        this.code = statusCode.getCode();
        //状态码的msg
        this.msg = statusCode.getMsg();
    }

    /**
     * 默认使用ERROR(500)状态码，自定义提示信息
     *
     * @param message
     */
    public LoginException(String message) {
        //手动设置异常时设置的message，异常错误的详情
        super(message);
        //默认使用ERROR(500)状态码
        this.code = CustomizeStatusCode.ERROR.getCode();
        this.msg = message;
    }
}