package com.project.sns.exception.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    EMPTY_PARAMETERS(HttpStatus.BAD_REQUEST, "Check parameters."),
    DUPLICATE_USER_NAME(HttpStatus.CONFLICT, "Username already exists."),
    NON_EXISTING_USER(HttpStatus.BAD_REQUEST, "User doesn't exist."),
    INCORRECT_PASSWORD(HttpStatus.UNAUTHORIZED, "Check password,"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad Request."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post Not Found."),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "Permission is invalid.");

    private HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
