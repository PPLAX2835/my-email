package xyz.pplax.mymail;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import xyz.pplax.mymail.mapper.EmailMapper;
import xyz.pplax.mymail.mapper.UserMapper;
import xyz.pplax.mymail.model.constants.EmailConstants;
import xyz.pplax.mymail.model.entity.Email;
import xyz.pplax.mymail.model.entity.Menu;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.mail.MailMessage;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.MailService;
import xyz.pplax.mymail.service.MenuService;
import xyz.pplax.mymail.service.UserService;
import xyz.pplax.mymail.utils.ReciveOneMail;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.*;

@SpringBootTest
class MymailApplicationTests {

    @Autowired
    MailService mailService;
    @Value("${pplax.file.savepath}")
    private String fileSavePath;

    /**
     * 分页查询 收件箱
     * @throws MessagingException
     */
    @Test
    public void pageMailSearchTest() throws MessagingException {
        /**
         * 假装这些是参数
         */
        MailMessage mailMessage = new MailMessage();
        mailMessage.setProtocol("imap");
        mailMessage.setHost("imap.qq.com");
        mailMessage.setEmailAddress("1458667357@qq.com");
        mailMessage.setEmailPassword("dvbviwakqsrvbadb");
        mailMessage.setPort("993");
        String type = EmailConstants.INBOX_FOLDER;
        int numberOfDays = 10;
        Date date = new Date();
        /*************************************/



        Properties props = new Properties(); // 参数配置
        props.setProperty("mail.transport.protocol", mailMessage.getProtocol()); // 使用的协议(JavaMail规范要求)
        props.setProperty("mail.smtp.host", mailMessage.getHost()); // 发件人的邮箱的SMTP服务器地址
        props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
        props.setProperty("mail.smtp.port", mailMessage.getPort());
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", mailMessage.getPort());

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
        calendar.setTime(date); // 日期
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

        System.out.println(JSON.toJSONString(mailMessageList));

    }


    @Test
    public void getSentMessages() throws MessagingException {
        /**
         * 假装这些是参数
         */
        MailMessage mailMessage = new MailMessage();
        mailMessage.setProtocol("pop3");
        mailMessage.setHost("pop.qq.com");
        mailMessage.setEmailAddress("1458667357@qq.com");
        mailMessage.setEmailPassword("dvbviwakqsrvbadb");
        mailMessage.setPort("995");
        String type = EmailConstants.INBOX_FOLDER;
        int numberOfDays = 10;
        Date date = new Date();
        /*************************************/



        Properties props = new Properties(); // 参数配置
        props.setProperty("mail.transport.protocol", mailMessage.getProtocol()); // 使用的协议(JavaMail规范要求)
        props.setProperty("mail.pop.host", mailMessage.getHost()); // 发件人的邮箱的SMTP服务器地址
        props.setProperty("mail.pop.auth", "true"); // 需要请求认证
        props.setProperty("mail.pop.port", mailMessage.getPort());
        props.setProperty("mail.pop.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.pop.socketFactory.fallback", "false");
        props.setProperty("mail.pop.socketFactory.port", mailMessage.getPort());

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
        calendar.setTime(date); // 日期
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

        System.out.println(JSON.toJSONString(mailMessageList));
    }


//    @Test
//    public void getSentMessageMailMessageListTest() throws MessagingException {
//
//        MailMessage mailMessage = new MailMessage();
//        mailMessage.setProtocol("smtp");
//        mailMessage.setHost("smtp.qq.com");
//        mailMessage.setEmailAddress("1458667357@qq.com");
//        mailMessage.setEmailPassword("dvbviwakqsrvbadb");
//
//        List<MailMessage> messages = mailService.getMessages(mailMessage, EmailConstants.SENT_MESSAGES_FOLDER);
//
//
//        for (MailMessage mailMessage1 : messages) {
//            System.out.println(JSON.toJSONString(mailMessage1));
//        }
//    }


    @Test
    public void sentEmailTest() throws MessagingException {
        MailMessage mailMessage = new MailMessage();

        mailMessage.setPort("587");
        mailMessage.setHost("smtp.qq.com");
        mailMessage.setEmailAddress("1458667357@qq.com");
        mailMessage.setEmailPassword("dvbviwakqsrvbadb");
        mailMessage.setSubject("这是一条标题");
        mailMessage.setText("这是一条内容");
        mailMessage.setProtocol("SMTP");
        mailMessage.setReceiverEmailAddress("lax1458667357@163.com");

        mailService.sendMailMessage(mailMessage);

    }

//    @Test
//    void sentEmailTest() {
//
////        String recipientEmail = "lax1458667357@163.com";
////        String subject = "这是一条标题";
////        String content = "这是一段内容";
////        MultipartFile attachment = null;
////
////        // 查询用户对应邮箱
////        Email email = new Email();
////        email.setEmailAddress("1458667357@qq.com");
////        email.setUid(7312L);
////        List<Email> emails = emailMapper.selectListSelective(email);
////        if (emails.size() == 0) {
////            throw new RuntimeException("找不到邮箱，可能是参数错误");
////        }
////        Email userEmail = emails.get(0);
//
//        MailMessage mailMessage = new MailMessage();
//        mailMessage.setEmailAddress("lax1458667357@163.com");
//        mailMessage.setPort("587");
//        mailMessage.setHost("smtp.qq.com");
//        mailMessage.setEmailPassword("dvbviwakqsrvbadb");
//
//
//        try {
////            if (attachment != null) {
////                byte[] attachmentBytes = attachment.getBytes();
////                mailService.sendMailMessage(mailMessage, "lax1458667357@163.com", "这是一条标题", "这是一段内容", attachmentBytes, attachment.getOriginalFilename());
////            } else {
//                mailService.sendMailMessage(mailMessage, "lax1458667357@163.com", "这是一条标题", "这是一段内容", null, null);
////            }
//            System.out.println(JSON.toJSONString(ResponseResult.success()));
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }


    @Autowired
    UserMapper userMapper;
    @Autowired
    EmailMapper emailMapper;
    @Test
    public void MapperTest() {
        User user = new User();
        user.setUid(1L);
        user.setUsername("PPLAX");
        user.setPassword("123456");
        user.setCreateTime(new Date());
        userMapper.insert(user);

        Email email = new Email();
        email.setEmailAddress("1458667357@qq.com");
        email.setEmailPassword("dvbviwakqsrvbadb");
        email.setUid(1L);
        email.setCreateTime(new Date());

        emailMapper.insert(email);
    }


    @Autowired
    UserService userService;
    @Test
    public void loginTest() {
        User user = userService.selectByUsernameAndPassword("PPLAX", "123456");
        System.out.println(JSON.toJSONString(user));
    }


    @Autowired
    MenuService menuService;
    @Test
    public void menuTest() {
        List<Menu> menus = menuService.selectParentMenuList();

        List<Menu> menus1 = menuService.selectListByPrimaryKey(1);

        Menu menu = new Menu();
        menu.setParentMenuId(1);
        List<Menu> menus2 = menuService.selectListSelective(menu);

        System.out.println(JSON.toJSONString(menus));
        System.out.println(JSON.toJSONString(menus1));
        System.out.println(menus2);
    }

}
