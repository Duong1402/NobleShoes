package com.example.datn.model.Response;

import com.example.datn.entity.HoaDonChiTiet;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HoaDonChiTietResponse {
//    private String hinhAnh;
    private String tenSanPham;
    private Integer soLuong;
    private String mauSac;
    private String size;
    private BigDecimal donGia;

    private BigDecimal thanhTien;

    public HoaDonChiTietResponse(HoaDonChiTiet hdct) {
//        this.hinhAnh = hdct.getChiTietSanPham().getSanPham().getHinhAnh().getUrl1();
        this.tenSanPham = hdct.getChiTietSanPham().getSanPham().getTen();
        this.soLuong = hdct.getSoLuong();
        this.mauSac = hdct.getChiTietSanPham().getMauSac().getTen();
        this.size = hdct.getChiTietSanPham().getSize().getTen();
        this.donGia = hdct.getDonGia();
        this.thanhTien = hdct.getThanhTien();
    }
}
