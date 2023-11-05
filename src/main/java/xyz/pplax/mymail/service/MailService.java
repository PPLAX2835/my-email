package xyz.pplax.mymail.service;

import com.alibaba.fastjson.JSON;
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
import xyz.pplax.mymail.model.constants.EmailConstants;
import xyz.pplax.mymail.model.mail.MailMessage;
import xyz.pplax.mymail.utils.ReciveOneMail;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * 邮件业务类
 * @author qzz
 */
@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private EmailMapper emailMapper;

    @Value("${pplax.file.savepath}")
    private String fileSavePath;


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
        } catch (MessagingException | IOException e) {
            return false;
        }

    }

    /**
     * 发送邮件
     */
    public void sendMailMessage(MailMessage mailMessage) throws MessagingException, IOException {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailMessage.getHost());
        javaMailSender.setPort(Integer.parseInt(mailMessage.getPort()));
        javaMailSender.setUsername(mailMessage.getEmailAddress());
        javaMailSender.setPassword(mailMessage.getEmailPassword());
        javaMailSender.setDefaultEncoding("UTF-8");
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.ssl.enable", "false");
        properties.setProperty("mail.smtp.ssl.required", "false");
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
            mimeMessageHelper.addAttachment(mailMessage.getAttachmentFileName(), new ByteArrayResource(mailMessage.getAttachment().getBytes()), Objects.requireNonNull(mailMessage.getAttachment().getContentType()));
        }

        //发送邮件
        javaMailSender.send(mimeMessageHelper.getMimeMessage());
        System.out.println("发送邮件成功："+mailMessage.getEmailAddress()+"->"+mailMessage.getReceiverEmailAddress());

    }


    /**
     * 获得收件箱的列表
     * @param mailMessage
     * @return
     * @throws MessagingException
     */
    public List<MailMessage> getInBoxMessages(MailMessage mailMessage, String type, Date beginDate, int numberOfDays) throws MessagingException {

        Properties props = new Properties(); // 参数配置
        props.setProperty("mail.store.protocol", mailMessage.getProtocol()); // 使用IMAP协议进行邮件接收
        props.setProperty("mail.imap.host", mailMessage.getHost()); // IMAP服务器地址
        props.setProperty("mail.imap.port", mailMessage.getPort()); // IMAP服务器端口号
        props.setProperty("mail.imap.ssl.enable", "true"); // 使用SSL加密连接

        // 建立会话
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        Store store = session.getStore(mailMessage.getProtocol());
        store.connect(mailMessage.getHost(), mailMessage.getEmailAddress(), mailMessage.getEmailPassword());// change the user and password accordingly

        Folder folder = store.getFolder(type);
        folder.open(Folder.READ_ONLY);

        // 定义邮件排序规则（按时间由近到远）
        // 计算开始日期（某一天）
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate); // 日期
        Date endDate = calendar.getTime();

        // 计算结束日期（当前日期的numberOfDays天前）
        calendar.add(Calendar.DAY_OF_MONTH, -numberOfDays);
        Date startDate = calendar.getTime();

        Date fromDate = calendar.getTime();
        SearchTerm searchTerm = new ReceivedDateTerm(ComparisonTerm.GT, fromDate);

        // 使用搜索条件过滤并分页查询邮件
        Message[] messages = folder.search(searchTerm);

        ReciveOneMail pmm = null;
        List<MailMessage> mailMessageList = new ArrayList<>();
        for (Message value : messages) {
            try {
                pmm = new ReciveOneMail((MimeMessage) value);

                MailMessage mailMessage1 = new MailMessage();
                mailMessage1.setSubject(pmm.getSubject());
                mailMessage1.setSentDate(pmm.getSentDate());
                mailMessage1.setReplySign(pmm.getReplySign());
                mailMessage1.setHasRead(pmm.isNew());
                mailMessage1.setHasAttachment(pmm.isContainAttach((Part) value));
                mailMessage1.setSenderEmailAddress(pmm.getFrom());
                mailMessage1.setReceiverEmailAddress(pmm.getMailAddress("to"));
                mailMessage1.setCcEmailAddress(pmm.getMailAddress("cc"));
                mailMessage1.setBccEmailAddress(pmm.getMailAddress("bcc"));
                mailMessage1.setMessageId(pmm.getMessageId());
                pmm.getMailContent((Part) value);
                mailMessage1.setText(pmm.getBodyText());

                pmm.setAttachPath(fileSavePath);
                pmm.saveAttachMent((Part) value);
                mailMessage1.setAttachmentFileName(pmm.getAttachName());

                mailMessageList.add(mailMessage1);

                System.out.println(JSON.toJSONString(mailMessage1));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }


        return mailMessageList;
    }

}

