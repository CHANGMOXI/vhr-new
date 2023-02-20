package org.changmoxi.vhr.controller.system.basic;

import org.apache.commons.lang3.ArrayUtils;
import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.common.enums.CustomizeStatusCode;
import org.changmoxi.vhr.common.exception.BusinessException;
import org.changmoxi.vhr.model.Position;
import org.changmoxi.vhr.service.PositionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-06 16:34
 **/
@RestController
@RequestMapping("/system/basic/positions")
public class PositionManagementController {
    @Resource
    private PositionService positionService;

    /**
     * 获取所有职位数据
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
        if (Objects.isNull(id)) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "id不能为空");
        }

        Integer[] ids = new Integer[1];
        ids[0] = id;
        return positionService.batchDeletePositions(ids);
    }

    /**
     * 批量删除职位数据
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/")
    public RespBean batchDeletePositions(Integer[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            throw new BusinessException(CustomizeStatusCode.PARAMETER_ERROR, "ids数组不能为空");
        }

        return positionService.batchDeletePositions(ids);
    }
}