package com.project.sns.exception;

import com.project.sns.exception.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SnsApplicationException extends RuntimeException {

    private ErrorCode errorCode;
    private String message;

    public SnsApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    @Override
    public String getMessage() {
        if (message == null || message.isBlank()) {
            return errorCode.getMessage();
        }
        return String.format("%s. %s", errorCode.getMessage(), this.message);
    }
}
