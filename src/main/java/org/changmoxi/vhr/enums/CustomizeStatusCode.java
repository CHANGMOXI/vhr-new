package org.changmoxi.vhr.enums;

import org.changmoxi.vhr.exception.StatusCode;

/**
 * 自定义业务状态码枚举类
 *
 * @author CZS
 * @create 2023-01-02 16:24
 **/
public enum CustomizeStatusCode implements StatusCode {
    SUCCESS(200),
    SUCCESS_ADD(200, "添加成功!"),
    SUCCESS_DELETE(200, "删除成功!"),
    SUCCESS_UPDATE(200, "更新成功!"),
    ERROR(500),
    ERROR_ADD(500, "添加失败!"),
    ERROR_DELETE(500, "删除失败!"),
    ERROR_UPDATE(500, "更新失败!"),

    PARAMETER_ERROR(5000, "传参有误或缺少必要参数!"),

    DATABASE_EXCEPTION(50000, "数据库异常，操作失败!"),
    DATABASE_VIOLATE_INTEGRITY_CONSTRAINT(50001, "该数据有关联数据，操作失败!"),

    EXIST_SAME_POSITION(50020, "已存在相同职位，添加失败!"),
    EXIST_EMPLOYEES_WITH_THIS_POSITION(50021, "要删除的职位存在员工，删除失败!"),
    EXIST_EMPLOYEES_WITH_ALL_POSITIONS(50022, "要删除的所有职位都存在员工，删除失败!"),
    UPDATE_SAME_POSITION(50023, "职位数据相同，更新失败!"),

    EXIST_SAME_JOBLEVEL(50030, "已存在相同职称，添加失败!"),
    EXIST_EMPLOYEES_WITH_THIS_JOBLEVEL(50031, "要删除的职称存在员工，删除失败!"),
    EXIST_EMPLOYEES_WITH_ALL_JOBLEVELS(50032, "要删除的所有职称都存在员工，删除失败!"),
    UPDATE_SAME_JOBLEVEL(50033, "职称数据相同，更新失败!");

    private Integer code;
    private String msg;

    /**
     * 用于默认的状态码，不设置msg，方便返回结果时自定义RespBean的msg属性
     *
     * @param code
     */
    CustomizeStatusCode(Integer code) {
        this.code = code;
        this.msg = null;
    }

    CustomizeStatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
