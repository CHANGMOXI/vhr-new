package org.changmoxi.vhr.controller.system.basic;

import org.apache.commons.lang3.ArrayUtils;
import org.changmoxi.vhr.enums.CustomizeStatusCode;
import org.changmoxi.vhr.exception.CustomizeException;
import org.changmoxi.vhr.model.JobLevel;
import org.changmoxi.vhr.model.RespBean;
import org.changmoxi.vhr.service.JobLevelService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-01-08 17:06
 **/
@RestController
@RequestMapping("/system/basic/joblevels")
public class JobLevelManagementController {
    @Resource
    private JobLevelService jobLevelService;

    /**
     * 返回所有职称数据
     *
     * @return
     */
    @GetMapping("/")
    public RespBean getAllJobLevels() {
        return jobLevelService.getAllJobLevels();
    }

    /**
     * 添加职称数据
     *
     * @param jobLevel
     * @return
     */
    @PostMapping("/")
    public RespBean addJobLevel(@RequestBody JobLevel jobLevel) {
        return jobLevelService.addJobLevel(jobLevel);
    }

    /**
     * 更新职称数据
     *
     * @param jobLevel
     * @return
     */
    @PutMapping("/")
    public RespBean updateJobLevel(@RequestBody JobLevel jobLevel) {
        return jobLevelService.updateJobLevel(jobLevel);
    }

    /**
     * 删除职称数据
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public RespBean deleteJobLevel(@PathVariable Integer id) {
        if (Objects.isNull(id)) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "id不能为空");
        }

        Integer[] ids = new Integer[1];
        ids[0] = id;
        return jobLevelService.batchDeleteJobLevels(ids);
    }

    /**
     * 批量删除职称数据
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/")
    public RespBean batchDeleteJobLevels(Integer[] ids) {
        if (ArrayUtils.isEmpty(ids)) {
            throw new CustomizeException(CustomizeStatusCode.PARAMETER_ERROR, "ids数组不能为空");
        }

        return jobLevelService.batchDeleteJobLevels(ids);
    }
}
