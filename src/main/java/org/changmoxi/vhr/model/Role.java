package org.changmoxi.vhr.model;

import lombok.Data;

@Data
public class Role {
    private Integer id;

    private String name;

    private String nameZh;

    private Boolean deleted;
}