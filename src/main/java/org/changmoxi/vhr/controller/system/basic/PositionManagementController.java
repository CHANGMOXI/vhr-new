package org.changmoxi.vhr.controller.system.basic;

import org.changmoxi.vhr.model.Position;
import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.service.PositionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author CZS
 * @create 2023-01-06 16:34
 **/
@RestController
@RequestMapping("/system/basic/position")
public class PositionManagementController {
    @Resource
    private PositionService positionService;

    /**
     * 返回所有职位数据
     *
     * @return
     */
    @GetMapping("/")
    public RespBean getAllPositions() {
        return positionService.getAllPositions();
    }

    /**
     * 添加职位数据
     *
     * @param position
     * @return
     */
    @PostMapping("/")
    public RespBean addPosition(@RequestBody Position position) {
        return positionService.addPosition(position);
    }

    /**
     * 更新职位数据
     *
     * @param position
     * @return
     */
    @PutMapping("/")
    public RespBean updatePosition(@RequestBody Position position) {
        return positionService.updatePosition(position);
    }

    /**
     * 删除职位数据
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public RespBean deletePosition(@PathVariable Integer id) {
        return positionService.deletePosition(id);
    }

    /**
     * 批量删除职位数据
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/")
    public RespBean batchDeletePositions(Integer[] ids) {
        return positionService.batchDeletePositions(ids);
    }
}
