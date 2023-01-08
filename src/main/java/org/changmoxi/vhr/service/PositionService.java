package org.changmoxi.vhr.service;

import org.changmoxi.vhr.model.Position;
import org.changmoxi.vhr.model.RespBean;

/**
 * @author CZS
 * @create 2023-01-06 16:42
 **/
public interface PositionService {
    /**
     * 获取所有职位数据
     *
     * @return
     */
    RespBean getAllPositions();

    /**
     * 添加职位数据
     *
     * @param position
     * @return
     */
    RespBean addPosition(Position position);

    /**
     * 更新职位数据
     *
     * @param position
     * @return
     */
    RespBean updatePosition(Position position);

    /**
     * 删除职位数据
     *
     * @param id
     * @return
     */
    RespBean deletePosition(Integer id);

    /**
     * 批量删除职位数据
     *
     * @param ids
     * @return
     */
    RespBean batchDeletePositions(Integer[] ids);
}
