package org.changmoxi.vhr.exception;

/**
 * 不同的业务service都有自己的异常，一般不能把所有业务异常定义在一起(否则这个类非常庞大)
 * 定义一个状态码的接口，只需实现这个接口，不同业务可以自定义不同的异常和状态码，方便管理
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
}
