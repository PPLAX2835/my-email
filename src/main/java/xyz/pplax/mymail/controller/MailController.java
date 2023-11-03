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
            mailMessage.setProtocol(EmailConstants.QQ_EMAIL_SEND_PROTOCOL);
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
            mailMessage.setProtocol(EmailConstants.QQ_EMAIL_SEND_PROTOCOL);
            mailMessage.setEmailAddress(emailAddress);
            mailMessage.setEmailPassword(email1.getEmailPassword());

            String suffix = emailAddress.substring(emailAddress.indexOf('@'), emailAddress.length());
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
}


//    @PostMapping(value = "/send")
//    public String sendTextMail(MessageDto messageDto){
//        String senderEmail = messageDto.getSenderEmail();
//
//        String recipientEmail = messageDto.getRecipientEmail();
//
//        String subject = messageDto.getSubject();
//
//        String content = messageDto.getContent();
//
//        MultipartFile attachment = messageDto.getAttachment();
//
//        try {
//            if (attachment != null) {
//                byte[] attachmentBytes = attachment.getBytes();
//                mailService.sendMailMessage(senderEmail, recipientEmail, subject, content, attachmentBytes, attachment.getOriginalFilename());
//            } else {
//                mailService.sendMailMessage(senderEmail, recipientEmail, subject, content, null, null);
//            }
//            return JSON.toJSONString(ResponseResult.success());
//        } catch (IOException e) {
//            return JSON.toJSONString(ResponseResult.error(e.getMessage()));
//        }
//    }


    /**
     * axios请求示例
     *
     *
             <div  style="width: 270px;height: 150px;overflow: hidden">
                     <el-upload
                     action="string"
                     :auto-upload="false" :on-change="testUpload"
                     :show-file-list="false">
                     <el-button>文件</el-button>
                 </el-upload>
             </div>
     *
     *
     *
     *   testUpload(file) {
     *       const formData = new FormData();
     *       formData.append('recipientEmail', '1470193239@qq.com');
     *       formData.append('subject', '这是一条标题');
     *       formData.append('content', "<p><img src='https://img-blog.csdnimg.cn/0aadf41df23145c79e7eb0e0b23b94f4.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAMTLnqIvluo_njL8=,size_20,color_FFFFFF,t_70,g_se,x_16' alt='在这里插入图片描述'><br> 收件邮件：<br> <img src='https://img-blog.csdnimg.cn/7b6c5581307c4976a7ea2c5bdf673bed.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBAMTLnqIvluo_njL8=,size_20,color_FFFFFF,t_70,g_se,x_16' alt='在这里插入图片描述'></p>");
     *       formData.append('attachment', file.raw);
     *
     *       // formData.append('messageDtoJsonStr', '{"recipientEmail": "1454847115@qq.com","subject": "这是一条标题","content": "..."}')
     *
     *
     *       console.log(file)
     *
     *       Axios.post('http://127.0.0.1:8080/api/messages/send', formData, {
     *       headers: {
     *           'Content-Type': 'multipart/form-data'
     *       }
     *       })
     *       .then(response => {
     *       console.log('Response:', response.data);
     *       })
     *       .catch(error => {
     *       console.error('Error:', error);
     *       });
     *     }
     *
     *
     *
     *
     *
     */
