package xyz.pplax.mymail.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MessageDto {

    private String senderEmail;

    private String recipientEmail;

    private String subject;

    private String content;

    private MultipartFile attachment;

}
