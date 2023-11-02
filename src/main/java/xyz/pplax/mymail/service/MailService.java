package xyz.pplax.mymail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.pplax.mymail.mapper.EmailMapper;
import xyz.pplax.mymail.model.entity.Email;
import xyz.pplax.mymail.model.mail.MailMessage;

import javax.mail.*;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

/**
 * 邮件业务类
 * @author qzz
 */
@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private EmailMapper emailMapper;


    /**
     * 检测邮件信息类
     * @param to
     * @param subject
     * @param text
     */
    private void checkMail(String to,String subject,String text){
        if(StringUtils.hasLength(to)){
            throw new RuntimeException("邮件收信人不能为空");
        }
        if(StringUtils.hasLength(subject)){
            throw new RuntimeException("邮件主题不能为空");
        }
        if(StringUtils.hasLength(text)){
            throw new RuntimeException("邮件内容不能为空");
        }
    }

    /**
     * 检查是否有效
     * @return
     * @throws GeneralSecurityException
     */
    public boolean checkSend(String host, String port, String emailAddress, String password) throws GeneralSecurityException {

        MailMessage mailMessage = new MailMessage();
        mailMessage.setPort(port);
        mailMessage.setHost(host);
        mailMessage.setEmailAddress(emailAddress);
        mailMessage.setEmailPassword(password);
        mailMessage.setSubject("这是一条标题");
        mailMessage.setText("这是一条内容");
        mailMessage.setProtocol("SMTP");
        mailMessage.setReceiverEmailAddress("lax1458667357@163.com");

        try {
            sendMailMessage(mailMessage);
            return true;
        } catch (MessagingException e) {
            return false;
        }

    }

    /**
     * 发送邮件
     */
    public void sendMailMessage(MailMessage mailMessage) throws MessagingException {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailMessage.getHost());
        javaMailSender.setPort(Integer.parseInt(mailMessage.getPort()));
        javaMailSender.setUsername(mailMessage.getEmailAddress());
        javaMailSender.setPassword(mailMessage.getEmailPassword());
        javaMailSender.setDefaultEncoding("UTF-8");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        javaMailSender.setJavaMailProperties(properties);


        /**
         * 建立邮件
         */
        //true 代表支持复杂的类型
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(),true);
        //邮件发信人
        mimeMessageHelper.setFrom(mailMessage.getEmailAddress());
        //邮件收信人  1或多个
        mimeMessageHelper.setTo(mailMessage.getReceiverEmailAddress().split(","));
        //邮件主题
        mimeMessageHelper.setSubject(mailMessage.getSubject());
        //邮件内容
        mimeMessageHelper.setText(mailMessage.getText());
        //邮件发送时间
        mimeMessageHelper.setSentDate(new Date());
        // 添加附件
        if (mailMessage.getAttachment() != null) {
            // Attach the file
            mimeMessageHelper.addAttachment(mailMessage.getAttachmentFileName(), new ByteArrayResource(mailMessage.getAttachment()));
        }

        //发送邮件
        javaMailSender.send(mimeMessageHelper.getMimeMessage());
        System.out.println("发送邮件成功："+mailMessage.getEmailAddress()+"->"+mailMessage.getReceiverEmailAddress());

    }
}

