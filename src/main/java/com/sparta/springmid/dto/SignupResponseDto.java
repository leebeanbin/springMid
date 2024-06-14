package com.sparta.areadevelopment.dto;

import lombok.Getter;

/**
 * 회원가입 로직후 내보내는 DTO
 */
@Getter
public class SignupResponseDto {

    /**
     * @String message 성공여부 메시지
     */
    private String message;

    /**
     * 생성자 메서드
     * @param message
     */
    public SignupResponseDto(String message) {
        this.message = message;
    }
}
