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
    private String tenSanPham;
    private int soLuongTon;
    private BigDecimal giaBan;
}