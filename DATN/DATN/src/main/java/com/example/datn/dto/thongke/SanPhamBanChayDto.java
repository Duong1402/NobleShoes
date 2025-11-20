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
public class SanPhamBanChayDto {
    private String hinhAnh;
    private String tenSanPham;
    private long soLuongBan;
    private BigDecimal giaTien;
}