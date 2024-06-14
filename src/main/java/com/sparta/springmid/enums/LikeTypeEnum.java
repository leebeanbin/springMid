package com.sparta.areadevelopment.enums;

/**
 * 좋아요 기능 적용 타입 관리 클래스.
 */
public enum LikeTypeEnum {
    BOARD("board"),
    COMMENT("comment");

    private final String contentType;

    LikeTypeEnum(String contentType) {
        this.contentType = contentType;
    }

    /**
     * 주어진 문자열이 이 열거형 인스턴스의 contentType 과 일치하는지 확인합니다.
     *
     * @param type 비교할 문자
     * @return 주어진 문자열이 contentType 과 일치하면 true, 그렇지 않으면 false
     */
    public boolean equalsType(String type) {
        return this.contentType.equalsIgnoreCase(type);
    }

    public static LikeTypeEnum fromContentType(String contentType) {
        for (LikeTypeEnum likeType : values()) {
            if (likeType.equalsType(contentType)) {
                return likeType;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 타입입니다.");
    }
}
