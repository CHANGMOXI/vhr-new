package org.changmoxi.vhr.common.exception;

import lombok.Getter;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;

/**
 * 自定义业务异常类
 *
 * @author CZS
 * @create 2023-01-07 11:36
 **/
@Getter
public class BusinessException extends RuntimeException {
    private Integer code;
    private String msg;

    /**
     * 手动设置异常
     *
     * @param statusCode
     * @param message
     */
    public BusinessException(StatusCode statusCode, String message) {
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
    public BusinessException(String message) {
        //手动设置异常时设置的message，异常错误的详情
        super(message);
        //默认使用ERROR(500)状态码
        this.code = CustomizeStatusCode.ERROR.getCode();
        this.msg = message;
    }
}
