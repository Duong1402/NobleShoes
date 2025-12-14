package com.example.datn.model.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String tenKhachHang;
    private String sdt;

    private String diaChiGiaoHang;

    private BigDecimal phiVanChuyen;

    private String tinhThanh;
    private String quanHuyen;
    private String phuongXa;
    private String diaChiCuThe;

    private BigDecimal tongTienSauGiam;
}
