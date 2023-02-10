package org.changmoxi.vhr.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import java.io.Serializable;
import java.util.Date;

/**
 * @author CZS
 * @create 2023-02-10 10:40
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailDTO implements Serializable {
    private String id;
    /**
     * 发送者
     */
    private String from;
    /**
     * 接收者，可以有多个
     */
    private String to;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件正文
     */
    private String text;
    /**
     * 发送时间
     */
    private Date sentDate;
    /**
     * 抄送人，可以有多个
     */
    private String cc;
    /**
     * 隐秘抄送人，可以有多个
     */
    private String bcc;
    /**
     * Template邮件模板的Html文件名称
     */
    private String template;
    /**
     * Template邮件模板的参数
     */
    private Context context;
    private String status;
    private String error;
    @JsonIgnore
    private MultipartFile[] multipartFiles;
}