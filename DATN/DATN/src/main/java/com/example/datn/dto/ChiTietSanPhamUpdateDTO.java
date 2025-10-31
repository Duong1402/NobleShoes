package com.example.datn.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ChiTietSanPhamUpdateDTO {
    private String tenSP;
    private UUID danhMucId;
    private UUID thuongHieuId;
    private UUID xuatXuId;
    private UUID mucDichSuDungId;

    private BigDecimal giaBan;
    private Integer soLuongTon;
    private String moTa;

    private UUID mauSacId;
    private UUID kichThuocId;
    private UUID chatLieuId;

    private String hinhAnhUrl;
}
