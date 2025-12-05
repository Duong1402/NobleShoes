package com.example.datn.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChiTietSanPhamDTO {

    private UUID id;              // ct.id
    private String maSP;          // sp.ma
    private String tenSP;         // sp.ten
    private String tenDanhMuc;    // d.ten
    private String tenThuongHieu; // t.ten
    private String tenXuatXu;     // x.ten
    private String tenDeGiay;     // dg.ten
    private String tenDayGiay;    // dy.ten
    private String tenMucDich;    // md.ten
    private BigDecimal giaBan;    // ct.giaBan
    private String moTa;          // ct.moTa
    private String mauSac;        // m.ten
    private String kichThuoc;     // k.ten
    private String chatLieu;      // cl.ten
    private String urlAnh1;       // h.urlAnh1
    private String urlAnh2;       // h.urlAnh2
    private String urlAnh3;       // h.urlAnh3
    private Integer soLuongTon;   // ct.soLuongTon

    // Constructor dùng cho JPQL: new com.example.datn.dto.ChiTietSanPhamDTO(...)
    public ChiTietSanPhamDTO(
            UUID id,
            String maSP,
            String tenSP,
            String tenDanhMuc,
            String tenThuongHieu,
            String tenXuatXu,
            String tenDeGiay,
            String tenDayGiay,
            String tenMucDich,
            BigDecimal giaBan,
            String moTa,
            String mauSac,
            String kichThuoc,
            String chatLieu,
            String urlAnh1,
            String urlAnh2,
            String urlAnh3,
            Integer soLuongTon
    ) {
        this.id = id;
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.tenDanhMuc = tenDanhMuc;
        this.tenThuongHieu = tenThuongHieu;
        this.tenXuatXu = tenXuatXu;
        this.tenDeGiay = tenDeGiay;
        this.tenDayGiay = tenDayGiay;
        this.tenMucDich = tenMucDich;
        this.giaBan = giaBan;
        this.moTa = moTa;
        this.mauSac = mauSac;
        this.kichThuoc = kichThuoc;
        this.chatLieu = chatLieu;
        this.urlAnh1 = urlAnh1;
        this.urlAnh2 = urlAnh2;
        this.urlAnh3 = urlAnh3;
        this.soLuongTon = soLuongTon;
    }
}
