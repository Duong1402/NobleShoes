package com.example.datn.dto.thongke;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamBanChayDto {
    private String hinhAnh;
    private String maSanPham;
    private String tenSanPham;
    private String mauSac;
    private String kichThuoc;
    private long soLuongBan;
}