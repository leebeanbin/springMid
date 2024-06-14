package com.sparta.areadevelopment.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.dao.DuplicateKeyException;

@Getter
public class FieldValidationException extends DuplicateKeyException {
    private Map<String, String> errors;

    public FieldValidationException(Map<String, String> errors) {
        super("Failed to signup.");
        this.errors = errors;
    }
}
