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
public class OverviewStatsDto {
    private BigDecimal tongDoanhThu = BigDecimal.ZERO;
    private long soDonThanhCong = 0;
    private long soDonHuy = 0;
    private long soSanPhamDaBan = 0;
    private long soDonTra = 0; // Tạm thời để 0
}