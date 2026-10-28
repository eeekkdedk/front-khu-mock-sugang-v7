package com.ncp03.coursechange.common.error;

/** 서비스에서 던지는 예외. GlobalExceptionHandler가 ErrorResponse로 바꿔 응답한다. */
public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        this(errorCode, errorCode.getDefaultMessage());
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
