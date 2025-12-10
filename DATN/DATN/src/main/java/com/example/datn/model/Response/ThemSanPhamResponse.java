package com.example.datn.model.Response;

import com.example.datn.entity.HoaDonChiTiet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThemSanPhamResponse {

    private HoaDonChiTiet hoaDonChiTiet;
    private String message;
}
