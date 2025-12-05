package com.example.datn.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String hoTen;
    private String email;
    private String sdt;
    private String taiKhoan; // username
    private String matKhau;  // password
}