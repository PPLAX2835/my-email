package xyz.pplax.mymail.model.mail;

import lombok.Data;

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

    private byte[] attachment;

    private String attachmentFileName;

    private String sentDate;

    private boolean replySign;

    private boolean hasRead;


}