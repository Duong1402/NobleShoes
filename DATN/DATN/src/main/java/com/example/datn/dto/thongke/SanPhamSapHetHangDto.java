package com.example.datn.dto.thongke;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamSapHetHangDto {
    private String hinhAnh;
    private String maSanPham;
    private String tenSanPham;
    private String mauSac;
    private String kichThuoc;
    private int soLuongTon;
}