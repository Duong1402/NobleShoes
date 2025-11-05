package com.example.datn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPhamResponse {
    private UUID id;
    private String ma;
    private String tenSanPham;
    private String mauSac;
    private String kichThuoc;
    private BigDecimal giaBan;
    private Integer soLuongTon;
}
