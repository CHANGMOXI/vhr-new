package org.changmoxi.vhr.model;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.exception.StatusCode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CZS
 * @create 2023-01-02 16:08
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private Integer code;
    private String msg;
    private Object data;

    /**
     * 默认使用SUCCESS(200)状态码，自定义msg
     *
     * @param msg
     * @return
     */
    public static RespBean ok(String msg) {
        return new RespBean(CustomizeStatusCode.SUCCESS.getCode(), msg, null);
    }

    /**
     * 默认使用SUCCESS(200)状态码，自定义msg和data
     *
     * @param msg
     * @param data
     * @return
     */
    public static RespBean ok(String msg, Object data) {
        return new RespBean(CustomizeStatusCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 使用指定的状态码和对应的提示
     *
     * @param statusCode
     * @return
     */
    public static RespBean ok(StatusCode statusCode) {
        return new RespBean(statusCode.getCode(), statusCode.getMsg(), null);
    }

    /**
     * 使用指定的状态码和对应的提示，自定义data
     *
     * @param statusCode
     * @param data
     * @return
     */
    public static RespBean ok(StatusCode statusCode, Object data) {
        return new RespBean(statusCode.getCode(), statusCode.getMsg(), data);
    }

    /**
     * 默认使用ERROR(500)状态码，自定义msg
     *
     * @param msg
     * @return
     */
    public static RespBean error(String msg) {
        return new RespBean(CustomizeStatusCode.ERROR.getCode(), msg, null);
    }

    /**
     * 使用自定义异常的状态码和对应的提示，用于手动抛出自定义异常后的全局异常处理的返回结果
     *
     * @param code
     * @param msg
     * @return
     */
    public static RespBean error(Integer code, String msg) {
        return new RespBean(code, msg, null);
    }

    /**
     * 使用自定义状态码和对应的提示
     *
     * @param statusCode
     * @return
     */
    public static RespBean error(StatusCode statusCode) {
        return new RespBean(statusCode.getCode(), statusCode.getMsg(), null);
    }

    /**
     * 使用自定义状态码和对应的提示，自定义data
     *
     * @param statusCode
     * @param data
     * @return
     */
    public static RespBean error(StatusCode statusCode, Object data) {
        return new RespBean(statusCode.getCode(), statusCode.getMsg(), data);
    }

    public static RespBean page(StatusCode statusCode, PageInfo<?> pageInfo) {
        Map<String, Object> data = new HashMap<>();
        data.put("pageNum", pageInfo.getPageNum());
        data.put("pageSize", pageInfo.getPageSize());
        data.put("total", pageInfo.getTotal());
        data.put("pages", pageInfo.getPages());
        data.put("list", pageInfo.getList());
        return new RespBean(statusCode.getCode(), statusCode.getMsg(), data);
    }
}
