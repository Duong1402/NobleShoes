package com.example.datn.model.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCustomerRequest {

    @NotBlank(message = "Họ tên không được trống")
    @JsonAlias({"hoTen", "fullName", "name"})
    private String hoTen;

    @NotBlank(message = "Tài khoản không được trống")
    @Size(min = 3, message = "Tài khoản tối thiểu 3 ký tự")
    @JsonAlias({"taiKhoan", "username", "userName"})
    private String taiKhoan;

    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    @JsonAlias({"matKhau", "password"})
    private String matKhau;

    @NotBlank(message = "Email không được trống")
    @Email(message = "Email không đúng định dạng")
    @JsonAlias({"email"})
    private String email;

    @NotBlank(message = "Số điện thoại không được trống")
    @JsonAlias({"sdt", "soDienThoai", "phone"})
    private String sdt;
}
