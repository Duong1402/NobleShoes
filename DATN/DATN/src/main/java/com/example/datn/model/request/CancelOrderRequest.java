package com.example.datn.model.request;

import lombok.Data;

@Data
public class CancelOrderRequest {
    private String reason;
    private String email;
    private String phone;
}
