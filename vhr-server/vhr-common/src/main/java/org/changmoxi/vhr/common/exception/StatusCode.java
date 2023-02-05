package org.changmoxi.vhr.common.exception;

/**
 * 状态码接口，实现该接口自定义业务状态码
 *
 * @author CZS
 * @create 2023-01-02 16:21
 **/
public interface StatusCode {
    /**
     * 返回状态码
     *
     * @return
     */
    Integer getCode();

    /**
     * 返回状态码的提示
     *
     * @return
     */
    String getMsg();
}
