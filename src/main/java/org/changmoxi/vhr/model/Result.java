package org.changmoxi.vhr.model;

import lombok.Data;
import org.changmoxi.vhr.enums.CustomizeStatusCode;

/**
 * @author CZS
 * @create 2023-01-02 16:08
 **/
@Data
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result success(String msg) {
        return new Result(CustomizeStatusCode.SUCCESS.getCode(), msg, null);
    }

    public static Result success(String msg, Object data) {
        return new Result(CustomizeStatusCode.SUCCESS.getCode(), msg, data);
    }

    public static Result error(String msg) {
        return new Result(CustomizeStatusCode.ERROR.getCode(), msg, null);
    }

    public static Result error(String msg, Object data) {
        return new Result(CustomizeStatusCode.ERROR.getCode(), msg, data);
    }

    private Result() {
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
