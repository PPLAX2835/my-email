package xyz.pplax.mymail.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import xyz.pplax.mymail.component.handler.CommonBlockHandler;
import xyz.pplax.mymail.model.constants.EmailConstants;
import xyz.pplax.mymail.model.constants.RedisKeyConstants;
import xyz.pplax.mymail.model.dto.MessageDto;
import xyz.pplax.mymail.model.entity.Email;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.mail.MailMessage;
import xyz.pplax.mymail.model.resp.ResponseCode;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.EmailService;
import xyz.pplax.mymail.service.MailService;
import xyz.pplax.mymail.service.UserService;

import org.thymeleaf.context.Context;
import xyz.pplax.mymail.utils.RedisOperator;

import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * 邮件收发相关
 */
@RestController
@RequestMapping("/api/messages")
@Api(value = "邮箱相关接口", tags = "MailController", description = "收发件箱查询、发送邮箱等")
public class MailController {

    @Value("${pplax.file.savepath}")
    private String fileSavePath;

    @Autowired
    private MailService mailService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RedisOperator redisOperator;

    private final TemplateEngine templateEngine;
    @Autowired
    public MailController(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * 获得收件箱
     * @param token
     * @param emailAddress
     * @return
     * @throws MessagingException
     */
    @GetMapping("inbox")
    @SentinelResource(value = "my-mail-api-resource", blockHandlerClass = CommonBlockHandler.class, blockHandler = "handle")
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
            return JSON.toJSONString(ResponseResult.success(mailService.getMessages(mailMessage, EmailConstants.INBOX_FOLDER, new Date(), 50)));

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
    @SentinelResource(value = "my-mail-api-resource", blockHandlerClass = CommonBlockHandler.class, blockHandler = "handle")
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
            return JSON.toJSONString(ResponseResult.success(mailService.getMessages(mailMessage, EmailConstants.SENT_MESSAGES_FOLDER, new Date(), 50)));

        }
    }


    /**
     * 下载附件
     * @param httpServletResponse
     * @param fileName
     * @throws IOException
     */
    @GetMapping("/attachment")
    @SentinelResource(value = "my-mail-api-resource", blockHandlerClass = CommonBlockHandler.class, blockHandler = "handle")
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


    /**
     * 发送邮件
     * @param httpServletRequest
     * @param attachment
     * @param messageDto
     * @return
     */
    @PostMapping(value = "/send")
    @SentinelResource(value = "my-mail-api-resource", blockHandlerClass = CommonBlockHandler.class, blockHandler = "handle")
    public String sendTextMail(HttpServletRequest httpServletRequest, @RequestParam(value = "attachment", required = false) MultipartFile attachment, MessageDto messageDto){

        String token = httpServletRequest.getHeader("token");
        User user = userService.selectByToken(token);
        Email record = new Email();
        record.setUid(user.getUid());
        record.setEmailAddress(messageDto.getSenderEmailAddress());
        List<Email> emails = emailService.selectListSelective(record);
        if (emails.size() == 0) {
            return JSON.toJSONString(ResponseResult.error("参数错误"));
        }


        // 检查发送方和接收方的收发次数，防止恶意攻击，不管有没有都要加一
        String sendCountStr = redisOperator.get(RedisKeyConstants.EMAIL_SEND_COUNT_PREFIX + messageDto.getSenderEmailAddress());
        String receiveCountStr = redisOperator.get(RedisKeyConstants.EMAIL_RECEIVE_COUNT_PREFIX + messageDto.getReceiverEmailAddress());
        // 处理发送方
        if (sendCountStr != null) {
            int sendCount = Integer.parseInt(sendCountStr);
            redisOperator.set(RedisKeyConstants.EMAIL_SEND_COUNT_PREFIX + messageDto.getSenderEmailAddress(), String.valueOf(sendCount + 1));
            // 先限制20次吧
            if (sendCount >= 20) {
                return JSON.toJSONString(ResponseResult.error(ResponseCode.OPERATION_ERROR, "使用的邮件发送太频繁"));
            }
        } else {
            redisOperator.set(RedisKeyConstants.EMAIL_SEND_COUNT_PREFIX + messageDto.getSenderEmailAddress(), "1");
        }
        // 处理接收方
        if (receiveCountStr != null) {
            int receiveCount = Integer.parseInt(receiveCountStr);
            redisOperator.set(RedisKeyConstants.EMAIL_RECEIVE_COUNT_PREFIX + messageDto.getReceiverEmailAddress(), String.valueOf(receiveCount + 1));
            // 先限制50次吧
            if (receiveCount >= 50) {
                return JSON.toJSONString(ResponseResult.error(ResponseCode.OPERATION_ERROR, "怀疑接收方被攻击了，请暂停发送"));
            }
        } else {
            redisOperator.set(RedisKeyConstants.EMAIL_RECEIVE_COUNT_PREFIX + messageDto.getReceiverEmailAddress(), "1");
        }


        // 封装参数
        MailMessage mailMessage = new MailMessage();
        mailMessage.setSenderEmailAddress(messageDto.getSenderEmailAddress());
        mailMessage.setEmailAddress(messageDto.getSenderEmailAddress());
        mailMessage.setEmailPassword(emails.get(0).getEmailPassword());
        mailMessage.setReceiverEmailAddress(messageDto.getReceiverEmailAddress());
        mailMessage.setSubject(messageDto.getSubject());
        // 转化成html页面
        Context context = new Context();
        context.setVariable("emailContent", messageDto.getContent());
        mailMessage.setText(templateEngine.process("mail", context));

        // 根据邮箱后缀设置参数
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

        // 发送邮件
        try {
            if (messageDto.isHasAttachment()) {

                mailMessage.setAttachment(attachment);
                mailMessage.setAttachmentFileName(messageDto.getAttachmentFileName());
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





