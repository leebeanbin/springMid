package com.sparta.areadevelopment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * 좋아요 기능을 수행하기 위한 정보들을 정의 해둔 클래스입니다.
 */
@Getter
public class LikeDto {

    /**
     * 컨텐츠의 고유번호입니다.
     */
    @NotNull(message = "Content ID는 필수입니다.")
    private Long contentId;

    /**
     * 컨텐츠의 타입입니다.
     */
    @NotBlank(message = "Content Type 은 필수입니다.")
    @Pattern(regexp = "[a-zA-Z]+", message = "영어만 입력 가능합니다.")
    private String contentType;
}
