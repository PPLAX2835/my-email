package xyz.pplax.mymail.model.constants;

/**
 * 缓存对应的一些键值，增加一些区分度，要不然以后可能有点乱
 */
public class RedisKeyConstants {

    /**
     * 存储用户数据时用这个
     */
    public static final String USER_INFO_PREFIX = "user:info:";


    /**
     * 邮件发送方的电子邮件地址，记录发送次数
     */
    public static final String EMAIL_SEND_COUNT_PREFIX = "email:send:count:";


    /**
     * 邮件接收方的电子邮件地址用这个，记录接收次数
     */
    public static final String EMAIL_RECEIVE_COUNT_PREFIX = "email:receive:count:";

}
