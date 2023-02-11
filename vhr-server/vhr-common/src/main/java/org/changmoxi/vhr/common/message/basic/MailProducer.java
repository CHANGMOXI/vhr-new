package org.changmoxi.vhr.common.message.basic;

import org.changmoxi.vhr.common.info.MailProducerInfo;
import org.changmoxi.vhr.common.utils.RocketMQUtil;
import org.changmoxi.vhr.dto.EmployeeMailDTO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author CZS
 * @create 2023-02-10 12:31
 **/
@Component
public class MailProducer {
    @Resource
    private RocketMQUtil rocketMQUtil;

    @Resource
    private MailProducerInfo mailProducerInfo;

    /**
     * 发送入职欢迎邮件
     *
     * @param employeeMailDTO
     * @return
     */
    public boolean sendWelcomeMail(EmployeeMailDTO employeeMailDTO) {
        return rocketMQUtil.syncSendMessage(mailProducerInfo.getTopic() + ":" + mailProducerInfo.getTagWelcome(), employeeMailDTO);
    }

    /**
     * 发送批量入职欢迎邮件
     *
     * @param employeeMailDTOList
     * @return
     */
    public boolean sendBatchWelcomeMails(List<EmployeeMailDTO> employeeMailDTOList) {
        return rocketMQUtil.syncSendBatchMessages(mailProducerInfo.getTopic() + ":" + mailProducerInfo.getTagWelcome(), employeeMailDTOList);
    }
}