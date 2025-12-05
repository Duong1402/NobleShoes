// src/main/java/com/example/datn/model/Response/HoaDonDetailResponse.java
package com.example.datn.model.Response;

import com.example.datn.entity.HoaDon;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class HoaDonDetailResponse extends HoaDonResponse {

    private List<HoaDonChiTietResponse> chiTietSanPham;
    private BigDecimal giamGia;

    public HoaDonDetailResponse(HoaDon hd, List<HoaDonChiTietResponse> chiTiet) {
        super(hd);
        this.chiTietSanPham = chiTiet;

        BigDecimal tt = hd.getTongTien() == null ? BigDecimal.ZERO : hd.getTongTien();
        BigDecimal ship = hd.getPhiVanChuyen() == null ? BigDecimal.ZERO : hd.getPhiVanChuyen();
        BigDecimal after = hd.getTongTienSauGiam() == null ? BigDecimal.ZERO : hd.getTongTienSauGiam();

        // giảm giá = tạm tính + ship - tổng sau giảm
        this.giamGia = tt.add(ship).subtract(after);
    }
}
