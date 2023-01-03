package org.changmoxi.vhr.enums;

import org.changmoxi.vhr.exception.StatusCode;

/**
 * 实现StatusCode接口，自定义业务状态码
 *
 * @author CZS
 * @create 2023-01-02 16:24
 **/
public enum CustomizeStatusCode implements StatusCode {
    SUCCESS(200),
    ERROR(500);

    private Integer code;

    CustomizeStatusCode(Integer code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
