package xyz.pplax.mymail;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@SpringBootTest
class MymailApplicationTests {

    @Autowired
    MailService mailService;

    @Test
    public void getInboxMailMessageListTest() throws MessagingException {

        MailMessage mailMessage = new MailMessage();
        mailMessage.setProtocol("pop3");
        mailMessage.setHost("imap.qq.com");
        mailMessage.setEmailAddress("1458667357@qq.com");
        mailMessage.setEmailPassword("dvbviwakqsrvbadb");

        List<MailMessage> messages = mailService.getMessages(mailMessage, EmailConstants.INBOX_FOLDER);


        for (MailMessage mailMessage1 : messages) {
            System.out.println(JSON.toJSONString(mailMessage1));
        }
    }


    @Test
    public void getSentMessages() {
        Properties props = new Properties(); // 参数配置
        props.setProperty("mail.transport.protocol", "smtp"); // 使用的协议(JavaMail规范要求)
        props.setProperty("mail.smtp.host", "smtp.qq.com"); // 发件人的邮箱的SMTP服务器地址
        props.setProperty("mail.smtp.auth", "true"); // 需要请求认证
        // PS:某些邮箱服务器要求SMTP连接需要使用SSL安全认证(为了提高安全性,邮箱支持SSL连接,也可以自己开启),
        // 如果无法连接邮件服务器,仔细查看控制台打印的 log,如果有有类似"连接失败,要求 SSL安全连接"等错误,
        // 开启 SSL安全连接
        // SMTP服务器的端口(非 SSL连接的端口一般默认为 25,可以不添加,如果开启了SSL连接,
        // 需要改为对应邮箱的SMTP服务器的端口,具体可查看对应邮箱服务的帮助,
        // QQ邮箱的SMTP(SLL)端口为465或587
        final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);
        try {
            Store store = session.getStore("imap");
            store.connect("smtp.qq.com", "1458667357@qq.com", "dvbviwakqsrvbadb");// change the user and password accordingly
            Folder folder = store.getFolder("Sent Messages");
//            Folder defaultFolder = store.getDefaultFolder();
//            Folder[] allFolder = defaultFolder.list();
            if (!folder.exists()) {
                System.out.println("inbox not found");
                System.exit(0);
            }
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            for (Message message : messages) {
                System.out.print(message.toString()+'\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void getSentMessageMailMessageListTest() throws MessagingException {

        MailMessage mailMessage = new MailMessage();
        mailMessage.setProtocol("smtp");
        mailMessage.setHost("smtp.qq.com");
        mailMessage.setEmailAddress("1458667357@qq.com");
        mailMessage.setEmailPassword("dvbviwakqsrvbadb");

        List<MailMessage> messages = mailService.getMessages(mailMessage, EmailConstants.SENT_MESSAGES_FOLDER);


        for (MailMessage mailMessage1 : messages) {
            System.out.println(JSON.toJSONString(mailMessage1));
        }
    }


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
