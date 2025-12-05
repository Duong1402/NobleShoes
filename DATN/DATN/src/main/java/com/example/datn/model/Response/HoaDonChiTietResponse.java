// src/main/java/com/example/datn/model/Response/HoaDonChiTietResponse.java
package com.example.datn.model.Response;

import com.example.datn.entity.HoaDonChiTiet;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class HoaDonChiTietResponse {

    private UUID id;
    private UUID chiTietSanPhamId;

    private String tenSanPham;
    private String size;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    // ✅ FE lấy ảnh từ đây
    private String urlAnh1;

    public HoaDonChiTietResponse() {}

    public HoaDonChiTietResponse(HoaDonChiTiet ct) {
        if (ct == null) return;

        this.id = ct.getId();
        this.tenSanPham = ct.getTenSanPham();
        this.size = ct.getSize();
        this.soLuong = ct.getSoLuong();
        this.donGia = ct.getDonGia();
        this.thanhTien = ct.getThanhTien();

        if (ct.getChiTietSanPham() != null) {
            this.chiTietSanPhamId = ct.getChiTietSanPham().getId();
        }
        // urlAnh1 sẽ được service bơm vào: r.setUrlAnh1(...)
    }
}
