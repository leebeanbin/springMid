package com.sparta.springmid.product.dto;

import lombok.Getter;

@Getter
public class PasswordChangeRequestDto {

    private String oldPassword;
    private String newPassword;
}
