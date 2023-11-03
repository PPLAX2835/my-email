package xyz.pplax.mymail.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.pplax.mymail.model.constants.EmailConstants;
import xyz.pplax.mymail.model.dto.MessageDto;
import xyz.pplax.mymail.model.entity.Email;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.mail.MailMessage;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.EmailService;
import xyz.pplax.mymail.service.MailService;
import xyz.pplax.mymail.service.UserService;

import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MailController {

    @Value("${pplax.file.savepath}")
    private String fileSavePath;

    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    /**
     * 获得收件箱
     * @param token
     * @param emailAddress
     * @return
     * @throws MessagingException
     */
    @GetMapping("inbox")
    public String getInbox(@RequestParam("token") String token, @RequestParam("emailAddress") String emailAddress) throws MessagingException {

        User user = userService.selectByToken(token);
        Email email = new Email();
        email.setUid(user.getUid());
        email.setEmailAddress(emailAddress);
        List<Email> emails = emailService.selectListSelective(email);

        if (emails.size() == 0) {
            return JSON.toJSONString(ResponseResult.error("参数错误"));
        } else {
            Email email1 = emails.get(0);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setProtocol(EmailConstants.QQ_EMAIL_SENT_PROTOCOL);
            mailMessage.setEmailAddress(emailAddress);
            mailMessage.setEmailPassword(email1.getEmailPassword());

            String suffix = emailAddress.substring(emailAddress.indexOf('@'), emailAddress.length());
            switch (suffix) {
                case EmailConstants.QQ_EMAIL_SUFFIX:
                    mailMessage.setHost(EmailConstants.QQ_EMAIL_RECEIVE_HOST);
                    mailMessage.setPort(EmailConstants.QQ_EMAIL_RECEIVE_PORT);
                    break;
                case EmailConstants.ALIYUN_EMAIL_SUFFIX:
                    mailMessage.setHost(EmailConstants.ALIYUN_EMAIL_RECEIVE_HOST);
                    mailMessage.setPort(EmailConstants.ALIYUN_EMAIL_RECEIVE_PORT);
                    break;
                case EmailConstants.SINA_EMAIL_SUFFIX:
                    mailMessage.setHost(EmailConstants.SINA_EMAIL_RECEIVE_HOST);
                    mailMessage.setPort(EmailConstants.SINA_EMAIL_RECEIVE_PORT);
                    break;
                case EmailConstants.NETEASE_EMAIL_SUFFIX:
                    mailMessage.setHost(EmailConstants.NETEASE_EMAIL_RECEIVE_HOST);
                    mailMessage.setPort(EmailConstants.NETEASE_EMAIL_RECEIVE_PORT);
                    break;
            }
            return JSON.toJSONString(ResponseResult.success(mailService.getInBoxMessages(mailMessage, EmailConstants.INBOX_FOLDER, new Date(), 50)));

        }
    }

    /**
     * 获得发件箱
     * @param token
     * @param emailAddress
     * @return
     * @throws MessagingException
     */
    @GetMapping("sent")
    public String getSent(@RequestParam("token") String token, @RequestParam("emailAddress") String emailAddress) throws MessagingException {

        User user = userService.selectByToken(token);
        Email email = new Email();
        email.setUid(user.getUid());
        email.setEmailAddress(emailAddress);
        List<Email> emails = emailService.selectListSelective(email);

        if (emails.size() == 0) {
            return JSON.toJSONString(ResponseResult.error("参数错误"));
        } else {
            Email email1 = emails.get(0);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setProtocol(EmailConstants.QQ_EMAIL_SENT_PROTOCOL);
            mailMessage.setEmailAddress(emailAddress);
            mailMessage.setEmailPassword(email1.getEmailPassword());

            String suffix = emailAddress.substring(emailAddress.indexOf('@'), emailAddress.length());
            switch (suffix) {
                case EmailConstants.QQ_EMAIL_SUFFIX:
                    mailMessage.setHost(EmailConstants.QQ_EMAIL_SENT_HOST);
                    mailMessage.setPort(EmailConstants.QQ_EMAIL_SENT_PORT);
                    break;
                case EmailConstants.ALIYUN_EMAIL_SUFFIX:
                    mailMessage.setHost(EmailConstants.ALIYUN_EMAIL_SENT_HOST);
                    mailMessage.setPort(EmailConstants.ALIYUN_EMAIL_SENT_PORT);
                    break;
                case EmailConstants.SINA_EMAIL_SUFFIX:
                    mailMessage.setHost(EmailConstants.SINA_EMAIL_SENT_HOST);
                    mailMessage.setPort(EmailConstants.SINA_EMAIL_SENT_PORT);
                    break;
                case EmailConstants.NETEASE_EMAIL_SUFFIX:
                    mailMessage.setHost(EmailConstants.NETEASE_EMAIL_SENT_HOST);
                    mailMessage.setPort(EmailConstants.NETEASE_EMAIL_SENT_PORT);
                    break;
            }
            return JSON.toJSONString(ResponseResult.success(mailService.getInBoxMessages(mailMessage, EmailConstants.SENT_MESSAGES_FOLDER, new Date(), 50)));

        }
    }


    /**
     * 下载附件
     * @param httpServletResponse
     * @param fileName
     * @throws IOException
     */
    @GetMapping("/attachment")
    public void downloadAttachment(HttpServletResponse httpServletResponse, @RequestParam("fileName") String fileName) throws IOException {

        System.out.println(fileName);

        // 读到流中
        InputStream inputStream = new FileInputStream(fileSavePath + fileName);// 文件的存放路径
        httpServletResponse.reset();
        httpServletResponse.setContentType("application/octet-stream");
        String filename = new File(fileSavePath + fileName).getName();
        httpServletResponse.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
        while ((len = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, len);
        }
        inputStream.close();
    }



    @PostMapping(value = "/send")
    public String sendTextMail(MessageDto messageDto){

        MailMessage mailMessage = new MailMessage();
        mailMessage.setSenderEmailAddress(messageDto.getSenderEmailAddress());
        mailMessage.setEmailAddress(messageDto.getSenderEmailAddress());
        mailMessage.setEmailPassword("dvbviwakqsrvbadb");
        mailMessage.setReceiverEmailAddress(messageDto.getReceiverEmailAddress());
        mailMessage.setSubject(messageDto.getSubject());
        mailMessage.setText(messageDto.getContent());

        String suffix = messageDto.getSenderEmailAddress().substring(messageDto.getSenderEmailAddress().indexOf('@'), messageDto.getSenderEmailAddress().length());
        switch (suffix) {
            case EmailConstants.QQ_EMAIL_SUFFIX:
                mailMessage.setHost(EmailConstants.QQ_EMAIL_SEND_HOST);
                mailMessage.setPort(EmailConstants.QQ_EMAIL_SEND_PORT);
                break;
            case EmailConstants.ALIYUN_EMAIL_SUFFIX:
                mailMessage.setHost(EmailConstants.ALIYUN_EMAIL_SEND_HOST);
                mailMessage.setPort(EmailConstants.ALIYUN_EMAIL_SEND_PORT);
                break;
            case EmailConstants.SINA_EMAIL_SUFFIX:
                mailMessage.setHost(EmailConstants.SINA_EMAIL_SEND_HOST);
                mailMessage.setPort(EmailConstants.SINA_EMAIL_SEND_PORT);
                break;
            case EmailConstants.NETEASE_EMAIL_SUFFIX:
                mailMessage.setHost(EmailConstants.NETEASE_EMAIL_SEND_HOST);
                mailMessage.setPort(EmailConstants.NETEASE_EMAIL_SEND_PORT);
                break;
        }

        try {
            if (messageDto.isHasAttachment()) {

                mailMessage.setAttachment(messageDto.getAttachment().getBytes());
                mailService.sendMailMessage(mailMessage);
            } else {
                mailService.sendMailMessage(mailMessage);
            }
            return JSON.toJSONString(ResponseResult.success());
        } catch (MessagingException | IOException e) {
            return JSON.toJSONString(ResponseResult.error(e.getMessage()));
        }

    }
}





