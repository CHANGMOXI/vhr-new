package org.changmoxi.vhr.dto;

import lombok.Data;
import org.changmoxi.vhr.model.Menu;

/**
 * 用于存放角色id和角色可操作的菜单项数据
 *
 * @author CZS
 * @create 2023-01-10 12:23
 **/
@Data
public class MenusByRoleDTO extends Menu{
    private Integer roleId;
}
