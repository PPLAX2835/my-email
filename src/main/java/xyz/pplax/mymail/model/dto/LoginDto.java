package xyz.pplax.mymail.model.dto;

import lombok.Data;

@Data
public class LoginDto {

    private String username;

    private String password;

    private String code;

}
