package xyz.pplax.mymail.model.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * email
 * @author 
 */
@Data
public class Email implements Serializable {
    /**
     * 主键
     */
    private Long emailId;

    /**
     * 外键，user
     */
    private Long uid;

    /**
     * 邮箱地址
     */
    private String emailAddress;

    /**
     * 邮箱密码或授权码
     */
    private String emailPassword;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}