package com.example.datn.model.Response;

import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.entity.HinhAnh;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.SanPham;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class HoaDonChiTietResponse {
    private UUID id;
    private String maSanPhamChiTiet;
    private String tenSanPham;
    private int soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    private String hinhAnhUrl;
    private String mauSac;
    private String size;

    public HoaDonChiTietResponse(HoaDonChiTiet hdct) {
        this.id = hdct.getId();
        this.soLuong = hdct.getSoLuong();
        this.donGia = hdct.getDonGia();
        this.thanhTien = hdct.getThanhTien();

        ChiTietSanPham ctsp = hdct.getChiTietSanPham();
        if (ctsp != null) {
            this.maSanPhamChiTiet = ctsp.getMa();

            if (ctsp.getMauSac() != null) {
                this.mauSac = ctsp.getMauSac().getTen();
            }

            if (ctsp.getKichThuoc() != null) {
                this.size = ctsp.getKichThuoc().getTen();
            }

            SanPham sanPham = ctsp.getSanPham();
            if (sanPham != null) {
                this.tenSanPham = sanPham.getTen();

                HinhAnh hinhAnh = sanPham.getHinhAnh();
                if (hinhAnh != null) {
                    this.hinhAnhUrl = hinhAnh.getUrlAnh1();
                }
            }
        }
    }
}