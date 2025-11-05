package com.example.datn.model.Response;

import com.example.datn.entity.HoaDonChiTiet;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HoaDonChiTietResponse {
    private String tenSanPham;
    private Integer soLuong;
    private BigDecimal donGia;

    private BigDecimal thanhTien;

    public HoaDonChiTietResponse(HoaDonChiTiet hdct) {

        this.tenSanPham = hdct.getChiTietSanPham().getSanPham().getTen();
        this.soLuong = hdct.getSoLuong();
        this.donGia = hdct.getDonGia();
        this.thanhTien = hdct.getThanhTien();
    }
}
