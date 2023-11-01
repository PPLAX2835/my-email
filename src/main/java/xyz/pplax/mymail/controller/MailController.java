package xyz.pplax.mymail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.pplax.mymail.model.dto.MessageDto;
import xyz.pplax.mymail.service.MailService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/messages")
public class MailController {

    @Autowired
    MailService mailService;

    @PostMapping(value = "/send")
    public void sendTextMail(
            MessageDto messageDto) throws IOException {

        String recipientEmail = messageDto.getRecipientEmail();

        String subject = messageDto.getSubject();

        String content = messageDto.getContent();

        MultipartFile attachment = messageDto.getAttachment();

        System.out.println(attachment);

        if (attachment != null) {
            byte[] attachmentBytes = attachment.getBytes();
            mailService.sendMailMessage(recipientEmail, subject, content, attachmentBytes, attachment.getOriginalFilename());
        } else {
            mailService.sendMailMessage(recipientEmail, subject, content, null, null);
        }
    }


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


}
