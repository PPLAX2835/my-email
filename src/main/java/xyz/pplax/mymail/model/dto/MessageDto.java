package xyz.pplax.mymail.model.dto;

import lombok.Data;

@Data
public class MessageDto {

    private String senderEmailAddress;

    private String receiverEmailAddress;

    private String subject;

    private String content;

    private boolean hasAttachment;

    private String attachmentFileName;

}
