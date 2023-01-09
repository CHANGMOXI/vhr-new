package org.changmoxi.vhr.service;

import org.changmoxi.vhr.model.JobLevel;
import org.changmoxi.vhr.model.RespBean;

/**
 * @author CZS
 * @create 2023-01-08 17:12
 **/
public interface JobLevelService {
    /**
     * 返回所有职称数据
     *
     * @return
     */
    RespBean getAllJobLevels();

    /**
     * 添加职称数据
     *
     * @param jobLevel
     * @return
     */
    RespBean addJobLevel(JobLevel jobLevel);

    /**
     * 更新职称数据
     *
     * @param jobLevel
     * @return
     */
    RespBean updateJobLevel(JobLevel jobLevel);

    /**
     * 批量删除职称数据
     *
     * @param ids
     * @return
     */
    RespBean batchDeleteJobLevels(Integer[] ids);
}
