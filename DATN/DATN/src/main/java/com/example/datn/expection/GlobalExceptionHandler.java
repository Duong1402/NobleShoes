package com.example.datn.expection;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Bắt lỗi validate @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        Map<String, Object> response = new HashMap<>();
        response.put("status", "FAILED");
        response.put("code", "VALIDATION_ERROR");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ✅ Lỗi nghiệp vụ custom
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "FAILED");
        response.put("code", ex.getCode());
        response.put("message", ex.getMessage());

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    // ✅ RuntimeException khác ApiException -> trả 400 dạng "timestamp..."
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {

        // ✅ Java 15 compatible: KHÔNG dùng "instanceof ApiException api"
        if (ex instanceof ApiException) {
            ApiException api = (ApiException) ex;

            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("code", api.getCode());
            response.put("message", api.getMessage());

            return ResponseEntity.status(api.getStatus()).body(response);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // ✅ Bắt tất cả lỗi còn lại (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        ex.printStackTrace();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "FAILED");
        response.put("code", "INTERNAL_ERROR");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
