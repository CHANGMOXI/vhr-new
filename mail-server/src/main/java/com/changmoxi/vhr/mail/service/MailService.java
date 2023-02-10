package com.changmoxi.vhr.mail.service;

import org.changmoxi.vhr.dto.MailDTO;

/**
 * @author CZS
 * @create 2023-02-10 10:33
 **/
public interface MailService {
    /**
     * 邮件信息校验
     *
     * @param mailDTO
     */
    void checkMail(MailDTO mailDTO);

    /**
     * 使用邮件模板发送邮件
     *
     * @param mailDTO
     * @return
     */
    MailDTO sendHtmlTemplateMail(MailDTO mailDTO);

    /**
     * 邮件发送成功后存储到数据库，方便统一和排查问题
     *
     * @param mailDTO
     * @return
     */
    MailDTO saveMail(MailDTO mailDTO);
}