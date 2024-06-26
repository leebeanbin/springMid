package com.sparta.springmid.product.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 유저 프로필 업테이트 DTO
 */
@Getter
public class UpdateUserDto {

    /**
     * @String nickname 유저 별명
     */
    @NotBlank(message = "Required Nickname")
    private String nickname;
    /**
     * @String email 이메일
     * @String info 한줄소개
     * @String password 비밀번호
     */
    @Email
    private String email;
    private String info;
    private String password;

}

