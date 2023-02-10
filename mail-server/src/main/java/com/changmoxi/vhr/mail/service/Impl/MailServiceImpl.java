package com.changmoxi.vhr.mail.service.Impl;

import com.changmoxi.vhr.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.changmoxi.vhr.dto.MailDTO;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Objects;

/**
 * @author CZS
 * @create 2023-02-10 11:14
 **/
@Slf4j
@Service
public class MailServiceImpl implements MailService {
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private MailProperties mailProperties;
    @Resource
    private TemplateEngine templateEngine;


    @Override
    public void checkMail(MailDTO mailDTO) {
        if (StringUtils.isBlank(mailDTO.getTo())) {
            throw new RuntimeException("邮件收信人不能为空");
        }
        if (StringUtils.isBlank(mailDTO.getSubject())) {
            throw new RuntimeException("邮件主题不能为空");
        }
//        if (StringUtils.isBlank(mailDTO.getText())) {
//            throw new RuntimeException("邮件内容不能为空");
//        }
        if (Objects.isNull(mailDTO.getSentDate())) {
            mailDTO.setSentDate(new Date());
        }
        mailDTO.setFrom(mailProperties.getUsername());
    }

    @Override
    public MailDTO sendHtmlTemplateMail(MailDTO mailDTO) {
        try {
            checkMail(mailDTO);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(mailDTO.getFrom());
            helper.setTo(mailDTO.getTo());
            helper.setSubject(mailDTO.getSubject());
            if (Objects.isNull(mailDTO.getSentDate())) {
                mailDTO.setSentDate(new Date());
            }
            helper.setSentDate(mailDTO.getSentDate());
            if (StringUtils.isNotBlank(mailDTO.getCc())) {
                helper.setCc(StringUtils.split(mailDTO.getCc(), ","));
            }
            if (StringUtils.isNotBlank(mailDTO.getBcc())) {
                helper.setBcc(StringUtils.split(mailDTO.getBcc(), ","));
            }
            if (ArrayUtils.isNotEmpty(mailDTO.getMultipartFiles())) {
                for (MultipartFile multipartFile : mailDTO.getMultipartFiles()) {
                    helper.addAttachment(multipartFile.getOriginalFilename(), multipartFile);
                }
            }
            String templateMail = templateEngine.process(mailDTO.getTemplate(), mailDTO.getContext());
            helper.setText(templateMail, true);
            javaMailSender.send(mimeMessage);
            mailDTO.setStatus("success");
            log.info("{} 邮件发送成功: {} -> {}", mailDTO.getSubject(), mailDTO.getFrom(), mailDTO.getTo());

            return saveMail(mailDTO);
        } catch (MessagingException e) {
            log.error("邮件发送失败: ", e);
            mailDTO.setStatus("fail");
            mailDTO.setError(e.getMessage());
            return mailDTO;
        }
    }

    @Override
    public MailDTO saveMail(MailDTO mailDTO) {
        // TODO 可以加上存储邮件到数据库的功能，便于统一和排查邮件问题
        return mailDTO;
    }
}