package org.changmoxi.vhr.model;

import lombok.Data;

import java.util.List;

@Data
public class Department {
    private Integer id;

    private String name;

    private Integer parentId;

    private String depPath;

    private Boolean enabled;

    /**
     * 实体类的布尔属性不能加 is，而数据库字段必须加 is_，要求在 resultMap 中进行字段与属性之间的映射
     */
    private Boolean parent;

    private Boolean deleted;

    /**
     * 该部门的子部门
     */
    private List<Department> children;
}