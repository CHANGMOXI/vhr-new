package org.changmoxi.vhr.common.message.basic;

import org.changmoxi.vhr.common.info.MailProducerInfo;
import org.changmoxi.vhr.common.utils.RocketMQUtil;
import org.changmoxi.vhr.model.Employee;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
     * @param employee
     * @return
     */
    public boolean sendWelcomeMail(Employee employee) {
        return rocketMQUtil.syncSendMessage(mailProducerInfo.getTopic() + ":" + mailProducerInfo.getTagWelcome(), employee);
    }
}