package org.changmoxi.vhr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class JobLevel {
    private Integer id;

    private String name;

    private String titleLevel;

    /**
     * 使用@JsonFormat，在实体类数据转换成JSON数据返回给前端时，格式化时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createDate;

    private Boolean enabled;

    /**
     * 逻辑删除: 0 未删除，1 已删除
     */
    private Boolean deleted;
}