package com.example.datn.model.Response;

import com.example.datn.entity.HoaDon;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class HoaDonDetailResponse extends HoaDonResponse {


    private String sdt;
    private String diaChiGiaoHang;
    private List<com.example.datn.model.Response.HoaDonChiTietResponse> chiTietSanPham;

    public HoaDonDetailResponse(HoaDon hd, List<HoaDonChiTietResponse> chiTiet) {
        super(hd);
        this.sdt = hd.getSdt();
        this.diaChiGiaoHang = hd.getDiaChiGiaoHang();
        this.chiTietSanPham = chiTiet;
    }
}