package org.changmoxi.vhr.exception;

import org.apache.ibatis.exceptions.PersistenceException;
import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.model.RespBean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理类
 *
 * @author CZS
 * @create 2023-01-07 12:05
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理SQL异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(SQLException.class)
    public RespBean sqlExceptionHandler(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            //外键关联导致的异常
            return RespBean.error(CustomizeStatusCode.DATABASE_VIOLATE_INTEGRITY_CONSTRAINT);
        }
        return RespBean.error(CustomizeStatusCode.DATABASE_EXCEPTION);
    }

    /**
     * 处理业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(CustomizeException.class)
    public RespBean customizeExceptionHandler(CustomizeException e) {
        //TODO log.error(e.getMessage(), e); 由于还没集成日志框架，暂且放着
        return RespBean.error(e.getCode(), e.getMsg());
    }

    /**
     * 处理MyBatis异常，包括事务异常等
     * IbatisException是MyBatis异常的顶级父类，但已过时
     * 代替它的是它的子类PersistenceException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(PersistenceException.class)
    public RespBean persistenceExceptionHandler(PersistenceException e) {
        //TODO log.error(e.getMessage(), e); 由于还没集成日志框架，暂且放着
        return RespBean.error(CustomizeStatusCode.DATABASE_EXCEPTION);
    }

    /**
     * 处理其他异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public RespBean exceptionHandler(Exception e) {
        //TODO log.error(e.getMessage(), e); 由于还没集成日志框架，暂且放着
        return RespBean.error(CustomizeStatusCode.ERROR_UNKNOWN);
    }
}