package com.example.datn.expection;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public ApiException(String message, String code, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = (status != null) ? status : HttpStatus.BAD_REQUEST;
    }

    public ApiException(String message, String code) {
        this(message, code, HttpStatus.BAD_REQUEST);
    }

    public ApiException(String message) {
        this(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
