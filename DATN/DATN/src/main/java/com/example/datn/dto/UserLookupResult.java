package com.example.datn.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLookupResult {
    private String taiKhoan;
    private String email;
    private String userType; // Giá trị: "EMPLOYEE" hoặc "CUSTOMER"
}
