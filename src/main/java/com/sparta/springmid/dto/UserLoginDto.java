package com.sparta.areadevelopment.dto;

import lombok.Data;

/**
 *  로그인을 위해 DTO
 */
@Data
public class UserLoginDto {

    /**
     * @String username 아이디
     * @String password 비밀번호
     */
    private String username;
    private String password;

}
