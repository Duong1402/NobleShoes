package com.example.datn.model.Response;

import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.entity.HinhAnh;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.SanPham;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HoaDonChiTietResponse {

    private String maSanPhamChiTiet;
    private String maSanPham;
    private String tenSanPham;
    private int soLuong;
    private BigDecimal donGia;
    private String mauSac;
    private BigDecimal thanhTien;
    private String size;

    private String hinhAnhUrl;
    private String kichThuoc;
    private String tenXuatXu;

    public HoaDonChiTietResponse(HoaDonChiTiet hdct) {

        this.maSanPham = hdct.getChiTietSanPham().getSanPham().getMa();
        this.tenSanPham = hdct.getChiTietSanPham().getSanPham().getTen();
        this.soLuong = hdct.getSoLuong();
        this.mauSac = hdct.getChiTietSanPham().getMauSac().getTen();
        this.size = hdct.getChiTietSanPham().getKichThuoc().getTen();
        this.kichThuoc = hdct.getChiTietSanPham().getKichThuoc().getTen();
        this.donGia = hdct.getDonGia();
        this.thanhTien = hdct.getThanhTien();

        ChiTietSanPham ctsp = hdct.getChiTietSanPham();
        if (ctsp != null) {
            this.maSanPhamChiTiet = ctsp.getMa();

            if (ctsp.getMauSac() != null) {
                this.mauSac = ctsp.getMauSac().getTen();
            }

            if (ctsp.getKichThuoc() != null) {
                this.kichThuoc = ctsp.getKichThuoc().getTen();
            }

            SanPham sanPham = ctsp.getSanPham();
            if (sanPham != null) {
                this.tenSanPham = sanPham.getTen();

                if (sanPham.getXuatXu() != null) {
                    this.tenXuatXu = sanPham.getXuatXu().getTen();
                }

                HinhAnh hinhAnh = sanPham.getHinhAnh();
                if (hinhAnh != null) {
                    this.hinhAnhUrl = hinhAnh.getUrlAnh1();
                }
            }
        }
    }
}