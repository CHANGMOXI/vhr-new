package org.changmoxi.vhr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author CZS
 * @create 2023-02-21 15:48
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailMessageLog {
    private Integer id;
    private String msgId;
    private Integer employeeId;
    private Integer status;
    private String topic;
    private String tag;
    private String brokerName;
    private Integer queueId;
    private Integer count;
    private Date tryTime;
    private Date createTime;
    private Date updateTime;
}
