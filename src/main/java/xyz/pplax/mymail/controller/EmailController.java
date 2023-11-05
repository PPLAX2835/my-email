package xyz.pplax.mymail.controller;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.pplax.mymail.model.constants.EmailConstants;
import xyz.pplax.mymail.model.dto.AddEmailDto;
import xyz.pplax.mymail.model.entity.Email;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.model.mail.MailMessage;
import xyz.pplax.mymail.model.resp.ResponseResult;
import xyz.pplax.mymail.service.EmailService;
import xyz.pplax.mymail.service.MailService;
import xyz.pplax.mymail.service.UserService;

import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;

/**
 * email相关，用于添加、删除邮箱等
 */
@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    UserService userService;
    @Autowired
    EmailService emailService;
    @Autowired
    MailService mailService;

    /**
     * 获得用户保存的邮箱
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/emails")
    public String getEmails(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        User user = userService.selectByToken(token);

        Email record = new Email();
        record.setUid(user.getUid());
        List<Email> emails = emailService.selectListSelective(record);

        System.out.println(JSON.toJSONString(emails));

        return JSON.toJSONString(ResponseResult.success(emails));
    }

    /**
     * 添加邮箱
     * @param httpServletRequest
     * @param addEmailDto
     * @return
     * @throws GeneralSecurityException
     */
    @PostMapping("/add")
    public String addEmail(HttpServletRequest httpServletRequest, AddEmailDto addEmailDto) throws GeneralSecurityException {
        String token = httpServletRequest.getHeader("token");
        User user = userService.selectByToken(token);

        Email email = new Email();
        email.setUid(user.getUid());
        email.setEmailAddress(addEmailDto.getEmailAddress());
        email.setCreateTime(new Date());
        email.setEmailPassword(addEmailDto.getEmailPassword());

        MailMessage mailMessage = new MailMessage();

        String host = addEmailDto.getType();
        switch (host) {
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

        boolean b = mailService.checkSend(mailMessage.getHost(), mailMessage.getPort().toString(), email.getEmailAddress(), email.getEmailPassword());

        if (b) {
            // 检查是否重复添加
            Email record = new Email();
            record.setUid(email.getUid());
            record.setEmailAddress(email.getEmailAddress());
            List<Email> emails = emailService.selectListSelective(record);
            if (emails.size() != 0) {
                // 说明已经添加过了
                return JSON.toJSONString(ResponseResult.error("请勿重复添加"));
            }

            emailService.insert(email);
            return JSON.toJSONString(ResponseResult.success());
        } else {
            return JSON.toJSONString(ResponseResult.error("验证失败，可能是授权码有误"));
        }
    }

}
