package com.sparta.springmid.product.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class TokenDto {
    /**
     * @String grantType  : JWT에 대한 인증 타입. Bearer
     * @String accessToken : 엑세스 토큰 저장
     * @String refreshToken : 리프레쉬 토큰 저장
     * @Boolean expired : 만료 여부
     */
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private boolean expired;
}
