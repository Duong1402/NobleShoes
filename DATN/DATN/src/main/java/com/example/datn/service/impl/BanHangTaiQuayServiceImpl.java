package com.example.datn.service.impl;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.KhachHang;
import com.example.datn.model.request.ThanhToanRequest;

import java.util.List;
import java.util.UUID;

public interface BanHangTaiQuayServiceImpl {

    HoaDon taoHoaDonCho(UUID idNhanVien);

    HoaDonChiTiet themSanPhamVaoHoaDon(UUID idHoaDon, UUID idChiTietSanPham, int soLuong);

    HoaDon capNhatKhachHang(UUID idHoaDon, UUID idKhachHang);

    HoaDon apDungPhieuGiamGia(UUID idHoaDon, UUID idPhieuGiamGia);

    HoaDon thanhToan(UUID idHoaDon, ThanhToanRequest request);

    List<HoaDonChiTiet> getChiTietHoaDon(UUID idHoaDon);

    void huyHoaDon(UUID idHoaDon);

    void xoaSanPhamKhoiHoaDon(UUID idHoaDon, UUID idChiTietSanPham);

    List<KhachHang> timKhachHangByHotenOrSdt(String keyword);
}
