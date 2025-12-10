package com.example.datn.expection;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    // ✅ code dạng string (chuẩn mới)
    private final String code;

    // ✅ status HTTP (chuẩn mới)
    private final HttpStatus status;

    // ✅ code dạng int (tương thích cũ). null nếu tạo theo string code
    private final Integer codeInt;

    // ===== Constructors (NEW) =====
    public ApiException(String message, String code, HttpStatus status) {
        super(message);
        this.code = (code == null || code.isBlank()) ? "BAD_REQUEST" : code;
        this.status = (status != null) ? status : HttpStatus.BAD_REQUEST;
        this.codeInt = null;
    }

    public ApiException(String message, String code) {
        this(message, code, HttpStatus.BAD_REQUEST);
    }

    public ApiException(String message) {
        this(message, "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }

    // ===== Constructor (OLD COMPAT) =====
    public ApiException(String message, int code) {
        super(message);
        this.codeInt = code;
        this.code = String.valueOf(code);
        // map nhanh: >=500 => 500, còn lại 400 (bạn có thể tự chỉnh logic)
        this.status = (code >= 500) ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.BAD_REQUEST;
    }

    // ===== Getters =====
    public String getCode() {
        return code; // string
    }

    public Integer getCodeInt() {
        return codeInt; // có thể null
    }

    public HttpStatus getStatus() {
        return status;
    }
}
