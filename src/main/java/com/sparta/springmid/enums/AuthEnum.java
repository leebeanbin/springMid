package com.sparta.areadevelopment.enums;


/**
 * 시큐리티관련 Enum 클래스
 */
public enum AuthEnum {

    /**
     * ACCESS_TOKEN ("Authorization"),   // 30분 REFRESH_TOKEN ("refresh-token"), // 2주 GRANT_TYPE
     * ("Bearer ");   //인증타입
     */
    ACCESS_TOKEN("Authorization"),
    REFRESH_TOKEN("refresh-token"),
    GRANT_TYPE("Bearer ");
    private String description;

    /**
     * 생성자 매서드
     *
     * @param description
     */
    AuthEnum(String token) {
        this.description = token;
    }

    /**
     * 내용 불러오는 매서드
     *
     * @return
     */
    public String getValue() {
        return description;
    }
}
