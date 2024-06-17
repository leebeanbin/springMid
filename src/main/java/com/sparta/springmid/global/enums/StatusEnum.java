package com.sparta.springmid.global.enums;

public enum StatusEnum {
    DELETED("DELETED"),
    ACTIVE("ACTIVE"),
    VERYFICATION("Verify");

    private String status;
    StatusEnum(String status) {
        this.status = status;
    }
}
