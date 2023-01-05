package org.changmoxi.vhr.model;

import lombok.Data;

/**
 * Menu实体类中的部分属性迁移到此类
 *
 * @author CZS
 * @create 2023-01-03 22:07
 **/
@Data
public class Meta {
    private Boolean keepAlive;
    private Boolean requireAuth;
}
