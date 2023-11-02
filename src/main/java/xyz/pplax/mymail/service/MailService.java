package xyz.pplax.mymail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.pplax.mymail.mapper.EmailMapper;
import xyz.pplax.mymail.model.entity.Email;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;
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
     * 发送邮件
     * @param to
     * @param subject
     * @param text
     * @param attachment
     * @param attachmentFileName
     */
    public void sendMailMessage(Email email, String to, String subject, String text, byte[] attachment, String attachmentFileName){

        try {

            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
            javaMailSender.setHost(email.getHost());
            javaMailSender.setPort(email.getPort());
            javaMailSender.setUsername(email.getEmailAddress());
            javaMailSender.setPassword(email.getEmailPassword());
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
            mimeMessageHelper.setFrom(email.getEmailAddress());
            //邮件收信人  1或多个
            mimeMessageHelper.setTo(to.split(","));
            //邮件主题
            mimeMessageHelper.setSubject(subject);
            //邮件内容
            mimeMessageHelper.setText(text);
            //邮件发送时间
            mimeMessageHelper.setSentDate(new Date());
            // 添加附件
            if (attachment != null) {
                // Attach the file
                mimeMessageHelper.addAttachment(attachmentFileName, new ByteArrayResource(attachment));
            }

            //发送邮件
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            System.out.println("发送邮件成功："+email.getEmailAddress()+"->"+to);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("发送邮件失败："+e.getMessage());
        }
    }
}

