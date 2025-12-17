package com.example.datn.model.Response;

import com.example.datn.entity.HoaDonChiTiet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThemSanPhamResponse {

    private HoaDonChiTiet hoaDonChiTiet;
    private List<HoaDonChiTiet> allRelatedHdct;
    private String message;
}
