package org.changmoxi.vhr.model;

import lombok.Data;

import java.util.List;

@Data
public class Menu {
    private Integer id;

    private String url;

    private String path;

    private String component;

    private String name;

    private String iconCls;

    /**
     * 原有的keepAlive、requireAuth属性迁移至Meta类
     */
    private Meta meta;

    private Integer parentId;

    private Boolean enabled;

    /**
     * 该菜单的子菜单
     */
    private List<Menu> children;

    /**
     * 菜单项所需的用户角色
     */
    private List<Role> roles;
}