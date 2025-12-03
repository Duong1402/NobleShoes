package com.example.datn.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class HoaDonFilterRequest {
    private String ma;
    private String tenKhachOrNhanVien;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayTu;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate ngayDen;

    private String loaiDon;
    private Integer trangThai;
}