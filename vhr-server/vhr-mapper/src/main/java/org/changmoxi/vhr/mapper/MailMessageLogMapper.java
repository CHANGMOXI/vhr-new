package org.changmoxi.vhr.mapper;

import org.changmoxi.vhr.model.MailMessageLog;

import java.util.List;

public interface MailMessageLogMapper {

    int insertSelective(MailMessageLog mailMessageLog);

    int batchInsert(List<MailMessageLog> mailMessageLogList);

    Integer selectIdByEmployeeId(Integer id);

    int updateStatusById(Integer id, Integer status);
}