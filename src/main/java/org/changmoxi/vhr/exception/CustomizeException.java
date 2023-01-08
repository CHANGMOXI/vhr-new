package org.changmoxi.vhr.exception;

import lombok.Getter;
import org.changmoxi.vhr.enums.CustomizeStatusCode;

/**
 * 自定义异常类
 *
 * @author CZS
 * @create 2023-01-07 11:36
 **/
@Getter
public class CustomizeException extends RuntimeException {
    private Integer code;
    private String msg;

    /**
     * 手动设置异常
     *
     * @param statusCode
     * @param message
     */
    public CustomizeException(StatusCode statusCode, String message) {
        //手动设置异常时设置的message，异常错误的详情
        super(message);
        //状态码
        this.code = statusCode.getCode();
        //状态码的msg
        this.msg = statusCode.getMsg();
    }

    /**
     * 默认使用ERROR(500)状态码
     *
     * @param message
     */
    public CustomizeException(String message) {
        //手动设置异常时设置的message，异常错误的详情
        super(message);
        //默认使用ERROR(500)状态码
        this.code = CustomizeStatusCode.ERROR.getCode();
        this.msg = CustomizeStatusCode.ERROR.getMsg();
    }
}
