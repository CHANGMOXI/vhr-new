package com.changmoxi.vhr.mail.basic;

import com.changmoxi.vhr.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.changmoxi.vhr.dto.MailDTO;
import org.changmoxi.vhr.model.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CZS
 * @create 2023-02-10 18:12
 **/
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${mail-consumer-info.consumerGroup}", topic = "${mail-consumer-info.topic}",
        selectorType = SelectorType.TAG, selectorExpression = "${mail-consumer-info.welcomeMailInfo.tagWelcome}")
public class WelcomeMailConsumer implements RocketMQListener<Employee> {
    @Resource
    private MailService mailService;

    @Value("${mail-consumer-info.welcomeMailInfo.templateWelcomeMail}")
    private String templateWelcomeMail;

    @Override
    public void onMessage(Employee message) {
        // TODO 消费者无法避免消息重复，需要业务服务来保证消息消费 幂等
        log.info("WelcomeMailConsumer 接收到消息: {}", message);
        Context context = new Context();
        context.setVariable("name", message.getName());
        context.setVariable("departmentName", message.getDepartmentName());
        context.setVariable("positionName", message.getPositionName());
        context.setVariable("jobLevelName", message.getJobLevelName());
        MailDTO mailDTO = MailDTO.builder().to(message.getEmail()).subject("入职欢迎").template(templateWelcomeMail).context(context).build();
        mailService.sendHtmlTemplateMail(mailDTO);
    }
}
