package xyz.pplax.mymail.model.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * email_log
 * @author 
 */
@Data
public class EmailLog implements Serializable {
    /**
     * 主键
     */
    private Long logId;

    /**
     * 外键，user
     */
    private Long uid;

    /**
     * 类型
     */
    private Object type;

    /**
     * 发件者邮箱
     */
    private String senderEmail;

    /**
     * 收件人邮箱
     */
    private String receiverEmail;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}