package org.changmoxi.vhr.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理类
 *
 * @author CZS
 * @create 2023-01-07 12:05
 **/
@Slf4j
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
        log.error("====================SQL异常====================");
        if (e instanceof SQLIntegrityConstraintViolationException) {
            //外键关联导致的异常
            log.error("====================外键关联异常====================");
            log.error(e.getMessage(), e);
            return RespBean.error(CustomizeStatusCode.DATABASE_VIOLATE_INTEGRITY_CONSTRAINT);
        }
        log.error(e.getMessage(), e);
        return RespBean.error(CustomizeStatusCode.DATABASE_EXCEPTION);
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
        log.error("====================MyBatis异常====================");
        log.error(e.getMessage(), e);
        return RespBean.error(CustomizeStatusCode.DATABASE_EXCEPTION);
    }

    /**
     * DAO异常，包括MyBatis等框架的异常
     * 比如日期数据错误Incorrect date value，对应DataAccessException异常的子类DataIntegrityViolationException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    public RespBean daoException(DataAccessException e) {
        log.error("====================DAO异常====================");
        log.error("异常类型:{}", e.getClass().toString());
        log.error(e.getMessage(), e);
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
        log.error("====================业务异常====================");
        log.error(e.getMessage(), e);
        return RespBean.error(e.getCode(), e.getMsg());
    }

    /**
     * 请求体(RequestBody)校验异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RespBean methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("====================请求体(RequestBody)校验异常====================");
        log.error("异常参数信息: {}", errors);
        log.error(e.getMessage(), e);
        return RespBean.error(CustomizeStatusCode.PARAMETER_ERROR, errors);
    }

    /**
     * 请求参数(PathVariable、RequestParam)校验异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public RespBean constraintViolationExceptionHandler(ConstraintViolationException e) {
        log.error("====================请求参数(PathVariable、RequestParam)校验异常====================");
        log.error("异常参数信息: {}", e.getMessage());
        log.error(e.getMessage(), e);
        return RespBean.error(CustomizeStatusCode.PARAMETER_ERROR, e.getMessage());
    }

    /**
     * 处理其他异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public RespBean exceptionHandler(Exception e) {
        log.error("====================未知异常====================");
        log.error(e.getMessage(), e);
        return RespBean.error(CustomizeStatusCode.ERROR_UNKNOWN);
    }
}