package com.sparta.areadevelopment.dto;

import lombok.Getter;

@Getter
public class PasswordChangeRequestDto {

    private String oldPassword;
    private String newPassword;
}
