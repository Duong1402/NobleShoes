package com.example.datn.model.Response;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.LichSuHoaDon;
import com.example.datn.entity.PhieuGiamGia;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class HoaDonDetailResponse extends HoaDonResponse {


    private List<HoaDonChiTietResponse> chiTietSanPham;

    // ✅ có/không có lịch sử đều dùng được
    private List<LichSuHoaDon> lichSuHoaDon;

    // ✅ giảm giá (tạm tính + ship - tổng sau giảm)
    private BigDecimal giamGia;

    private PhieuGiamGiaResponse phieuGiamGiaResponse;

    public HoaDonDetailResponse(HoaDon hd, List<HoaDonChiTietResponse> chiTiet) {
        super(hd);
        this.chiTietSanPham = chiTiet;
        this.giamGia = calcGiamGia(hd);
        this.phieuGiamGiaResponse = mapPhieuGiamGia(hd.getPhieuGiamGia());
    }

    public HoaDonDetailResponse(HoaDon hd, List<HoaDonChiTietResponse> chiTiet, List<LichSuHoaDon> lichSuHoaDon) {
        super(hd);
        this.chiTietSanPham = chiTiet;
        this.lichSuHoaDon = lichSuHoaDon;
        this.giamGia = calcGiamGia(hd);
        this.phieuGiamGiaResponse = mapPhieuGiamGia(hd.getPhieuGiamGia());
    }

    private PhieuGiamGiaResponse mapPhieuGiamGia(PhieuGiamGia pgg) {
        if (pgg == null) {
            return null;
        }
        return new PhieuGiamGiaResponse(
                pgg.getMa(),
                pgg.getTen(),
                pgg.getGiaTriGiam()
        );
    }

    private BigDecimal calcGiamGia(HoaDon hd) {
        if (hd == null) return BigDecimal.ZERO;

        BigDecimal tt = hd.getTongTien() == null ? BigDecimal.ZERO : hd.getTongTien();
        BigDecimal ship = hd.getPhiVanChuyen() == null ? BigDecimal.ZERO : hd.getPhiVanChuyen();
        BigDecimal after = hd.getTongTienSauGiam() == null ? BigDecimal.ZERO : hd.getTongTienSauGiam();

        return tt.add(ship).subtract(after);
    }
}