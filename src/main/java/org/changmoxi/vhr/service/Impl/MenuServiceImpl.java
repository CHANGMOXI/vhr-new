package org.changmoxi.vhr.service.Impl;

import org.changmoxi.vhr.mapper.MenuMapper;
import org.changmoxi.vhr.model.Hr;
import org.changmoxi.vhr.model.Menu;
import org.changmoxi.vhr.service.MenuService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CZS
 * @create 2023-01-03 22:29
 **/
@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    @Override
//    @Cacheable//TODO 菜单项数据很少变化，每次请求都查询数据库不太合理，可以加入缓存Spring Cache 或 Redis
    public List<Menu> getMenusByHrId() {
        Integer id = ((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        return menuMapper.getMenusByHrId(id);
    }

    @Override
//    @Cacheable//TODO 菜单项数据很少变化，每次请求都查询数据库不太合理，可以加入缓存Spring Cache 或 Redis
    public List<Menu> getAllMenusWithRoles() {
        return menuMapper.getAllMenusWithRoles();
    }
}
