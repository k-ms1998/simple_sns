package com.project.sns.exception.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    EMPTY_PARAMETERS(HttpStatus.BAD_REQUEST, "Check parameters."),
    DUPLICATE_USER_NAME(HttpStatus.CONFLICT, "Username already exists."),
    NON_EXISTING_USER(HttpStatus.BAD_REQUEST, "User doesn't exist."),
    INCORRECT_PASSWORD(HttpStatus.BAD_REQUEST, "Check password,");

    private HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
