package xyz.pplax.mymail.model.mail;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MailMessage {

    private String messageId;

    private String host;

    private String port;

    private String protocol;

    private String emailAddress;

    private String emailPassword;

    private String senderEmailAddress;

    private String receiverEmailAddress;

    private String ccEmailAddress;

    private String bccEmailAddress;

    private String subject;

    private String text;

    private boolean hasAttachment;

    private MultipartFile attachment;

    private String attachmentFileName;

    private String sentDate;

    private boolean replySign;

    private boolean hasRead;


}