package com.stone.aiexam.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;

    private String realName;

    private String password;

    private String rePassword;
}
