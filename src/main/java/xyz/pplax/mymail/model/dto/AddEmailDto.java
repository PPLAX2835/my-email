package xyz.pplax.mymail.model.dto;

import lombok.Data;

@Data
public class AddEmailDto {

    private String emailAddress;

    private String emailPassword;

    private String type;

}
