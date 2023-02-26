package org.changmoxi.vhr.service;

import org.changmoxi.vhr.common.RespBean;
import org.changmoxi.vhr.model.JobLevel;

import java.util.List;

/**
 * @author CZS
 * @create 2023-01-08 17:12
 **/
public interface JobLevelService {
    /**
     * 获取所有职称数据
     *
     * @return
     */
    List<JobLevel> getAllJobLevels();

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