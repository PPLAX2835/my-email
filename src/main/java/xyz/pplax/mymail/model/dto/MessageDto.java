package xyz.pplax.mymail.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MessageDto {

    private String senderEmailAddress;

    private String receiverEmailAddress;

    private String subject;

    private String content;

    private boolean hasAttachment;

    private String attachmentFileName;

    private MultipartFile attachment;

}
