package xyz.pplax.mymail.model.mail;

import lombok.Data;

@Data
public class MailMessage {

    private String host;

    private String port;

    private String protocol;

    private String emailAddress;

    private String emailPassword;

    private String senderEmailAddress;

    private String receiverEmailAddress;

    private String subject;

    private String text;

    private byte[] attachment;

    private String attachmentFileName;

}