package org.changmoxi.vhr.common.message.basic;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.changmoxi.vhr.common.info.MailProducerInfo;
import org.changmoxi.vhr.common.utils.RocketMQUtil;
import org.changmoxi.vhr.dto.EmployeeMailDTO;
import org.changmoxi.vhr.mapper.MailMessageLogMapper;
import org.changmoxi.vhr.model.MailMessageLog;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author CZS
 * @create 2023-02-10 12:31
 **/
@Slf4j
@Component
public class MailProducer {
    @Resource
    private RocketMQUtil rocketMQUtil;

    @Resource
    private MailProducerInfo mailProducerInfo;

    @Resource
    private MailMessageLogMapper mailMessageLogMapper;

    /**
     * 发送入职欢迎邮件
     *
     * @param employeeMailDTO
     * @return
     */
    public boolean sendWelcomeMail(EmployeeMailDTO employeeMailDTO) {
        // RocketMQ发消息
        SendResult sendResult = rocketMQUtil.syncSendMessage(mailProducerInfo.getTopic() + ":" + mailProducerInfo.getTagWelcome(), employeeMailDTO);
        // 发送后记录消息
        saveMailSendLog(sendResult, employeeMailDTO.getId(), null);
        return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
    }

    /**
     * 发送批量入职欢迎邮件
     *
     * @param employeeMailDTOList
     * @return
     */
    public boolean sendBatchWelcomeMails(List<EmployeeMailDTO> employeeMailDTOList) {
        // RocketMQ批量发消息
        SendResult sendResult = rocketMQUtil.syncSendBatchMessages(mailProducerInfo.getTopic() + ":" + mailProducerInfo.getTagWelcome(), employeeMailDTOList);
        // 发送完后记录消息
        saveMailSendLog(sendResult, null, employeeMailDTOList);
        return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
    }

    private boolean saveMailSendLog(SendResult sendResult, Integer employeeId, List<EmployeeMailDTO> employeeMailDTOList) {
        // 记录邮件发送消息
        if (CollectionUtils.isEmpty(employeeMailDTOList)) {
            log.info("开始存储邮件发送消息记录，employeeId:{}，msgId:{}", employeeId, sendResult.getMsgId());
            Date now = new Date();
            MailMessageLog mailMessageLog = MailMessageLog.builder().msgId(sendResult.getMsgId())
                    .employeeId(employeeId)
                    .status(SendStatus.SEND_OK.equals(sendResult.getSendStatus()) ? 1 : 0)
                    .topic(mailProducerInfo.getTopic())
                    .tag(mailProducerInfo.getTagWelcome())
                    .brokerName(sendResult.getMessageQueue().getBrokerName())
                    .queueId(sendResult.getMessageQueue().getQueueId())
                    .createTime(now)
                    .updateTime(now)
                    .build();
            if (mailMessageLogMapper.insertSelective(mailMessageLog) == 1) {
                log.info("存储邮件发送消息记录成功!");
                return true;
            } else {
                log.error("存储邮件发送消息记录失败!");
                return false;
            }
        } else {
            Integer[] employeeIds = employeeMailDTOList.stream().map(EmployeeMailDTO::getId).toArray(Integer[]::new);
            String[] msgIds = StringUtils.split(sendResult.getMsgId(), ",");
            log.info("开始批量存储邮件发送消息记录，employeeIds:{}，msgIds:{}", employeeIds, msgIds);
            Date now = new Date();
            List<MailMessageLog> mailMessageLogList = new ArrayList<>();
            for (int i = 0; i < employeeIds.length; i++) {
                MailMessageLog mailMessageLog = MailMessageLog.builder().msgId(msgIds[i])
                        .employeeId(employeeIds[i])
                        .status(SendStatus.SEND_OK.equals(sendResult.getSendStatus()) ? 1 : 0)
                        .topic(mailProducerInfo.getTopic())
                        .tag(mailProducerInfo.getTagWelcome())
                        .brokerName(sendResult.getMessageQueue().getBrokerName())
                        .queueId(sendResult.getMessageQueue().getQueueId())
                        .createTime(now)
                        .updateTime(now)
                        .build();
                mailMessageLogList.add(mailMessageLog);
            }
            if (mailMessageLogMapper.batchInsert(mailMessageLogList) > 0) {
                log.info("批量存储邮件发送消息记录成功!");
                return true;
            } else {
                log.error("批量存储邮件发送消息记录失败!");
                return false;
            }
        }
    }
}