package com.example.datn.model.Response;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.LichSuHoaDon;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class HoaDonDetailResponse extends HoaDonResponse {


    private List<com.example.datn.model.Response.HoaDonChiTietResponse> chiTietSanPham;
    private List<LichSuHoaDon> lichSuHoaDon;

    public HoaDonDetailResponse(HoaDon hd, List<HoaDonChiTietResponse> chiTiet, List<LichSuHoaDon> lichSuHoaDon) {
        super(hd);
        this.chiTietSanPham = chiTiet;
        this.lichSuHoaDon = lichSuHoaDon;
    }
}