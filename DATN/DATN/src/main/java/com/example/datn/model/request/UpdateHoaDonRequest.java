package com.example.datn.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateHoaDonRequest {
    private Integer trangThai;
    private String sdt;
    private String diaChiGiaoHang;
}