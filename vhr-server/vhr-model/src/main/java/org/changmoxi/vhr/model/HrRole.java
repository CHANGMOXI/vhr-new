package org.changmoxi.vhr.model;

import lombok.Data;

@Data
public class HrRole {
    private Integer id;

    private Integer hrId;

    private Integer rId;

    private Boolean enabled;
}