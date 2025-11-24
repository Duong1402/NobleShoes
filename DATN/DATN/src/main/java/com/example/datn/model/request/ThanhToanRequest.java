package com.example.datn.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToanRequest {
    private UUID idPhuongThucThanhToan;
    private String loaiHoaDon; // "Online" hoặc "Tại cửa hàng"

    // Các trường này có thể null nếu bán tại quầy
    private String tenKhachHang;
    private String sdt;
    private String diaChiGiaoHang;
    private BigDecimal phiVanChuyen;
}
