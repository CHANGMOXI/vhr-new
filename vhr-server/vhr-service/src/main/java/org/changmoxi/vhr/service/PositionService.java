package org.changmoxi.vhr.service;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.model.Position;

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
     * 批量删除职位数据
     *
     * @param ids
     * @return
     */
    RespBean batchDeletePositions(Integer[] ids);
}
