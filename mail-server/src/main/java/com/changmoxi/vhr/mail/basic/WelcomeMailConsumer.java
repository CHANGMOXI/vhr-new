package com.changmoxi.vhr.mail.basic;

import com.changmoxi.vhr.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.changmoxi.vhr.dto.EmployeeMailDTO;
import org.changmoxi.vhr.dto.MailDTO;
import org.changmoxi.vhr.mapper.MailMessageLogMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-02-10 18:12
 **/
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${mail-consumer-info.consumerGroup}", topic = "${mail-consumer-info.topic}",
        selectorType = SelectorType.TAG, selectorExpression = "${mail-consumer-info.welcomeMailInfo.tagWelcome}")
public class WelcomeMailConsumer implements RocketMQListener<EmployeeMailDTO> {
    @Resource
    private MailService mailService;

    @Resource
    private MailMessageLogMapper mailMessageLogMapper;

    @Value("${mail-consumer-info.welcomeMailInfo.templateWelcomeMail}")
    private String templateWelcomeMail;

    private static final Integer CONSUME_SUCCESS = 2;

    @Override
    public void onMessage(EmployeeMailDTO message) {
        // TODO 消费者无法避免消息重复，需要业务服务来保证消息消费 幂等
        log.info("WelcomeMailConsumer 接收到消息: {}", message);
        Context context = new Context();
        context.setVariable("name", message.getName());
        context.setVariable("departmentName", message.getDepartmentName());
        context.setVariable("positionName", message.getPositionName());
        context.setVariable("jobLevelName", message.getJobLevelName());
        MailDTO mailDTO = MailDTO.builder().to(message.getEmail()).subject("入职欢迎").template(templateWelcomeMail).context(context).build();
        mailService.sendHtmlTemplateMail(mailDTO);

        // 消费完之后，设置消息记录的状态为消费成功
        try {
            for (int i = 0; i < 10; i++) {
                Integer id = mailMessageLogMapper.selectIdByEmployeeId(message.getId());
                if (Objects.nonNull(id)) {
                    if (mailMessageLogMapper.updateStatusById(id, CONSUME_SUCCESS) == 1) {
                        break;
                    } else {
                        // TODO 不确定这里抛异常会不会继续重试，按理说是会的
                        throw new RuntimeException("更新消息记录的发送状态失败，稍后重试");
                    }
                }
                // TODO 待完善，可以做成异步或使用其他方式，避免堵塞在这里
                Thread.sleep(500);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}