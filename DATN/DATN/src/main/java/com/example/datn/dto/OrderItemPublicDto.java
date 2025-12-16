package com.example.datn.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class OrderItemPublicDto {
    private UUID chiTietSanPhamId;
    private String tenSanPham;
    private Integer soLuong;
    private BigDecimal donGia;
    private String urlAnh1;
    private String maVach;

    public OrderItemPublicDto(UUID ctspId, String ten, Integer sl, BigDecimal gia, String urlAnh1, String maVach) {
        this.chiTietSanPhamId = ctspId;
        this.tenSanPham = ten;
        this.soLuong = sl;
        this.donGia = gia;
        this.urlAnh1 = urlAnh1;
        this.maVach = maVach;
    }
}
