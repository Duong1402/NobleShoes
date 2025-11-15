package com.example.datn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ChiTietSanPhamDTO {

    private UUID id; // ID chi tiết sản phẩm
    private String maSP;
    private String tenSP;

    private String tenDanhMuc;
    private String tenThuongHieu;
    private String tenXuatXu;
    private String tenDayGiay;
    private String tenDeGiay;
    private String mucDichSuDung;

    private BigDecimal giaBan;
    private String moTa;

    private String mauSac;
    private String kichThuoc;
    private String chatLieu;

    // URL ảnh: có thể 1, 2 hoặc 3 ảnh
    private String urlAnh1;
    private String urlAnh2;
    private String urlAnh3;

    public ChiTietSanPhamDTO(UUID id, String maSP, String tenSP,
                             String tenDanhMuc, String tenThuongHieu, String tenXuatXu,
                             String tenDayGiay, String tenDeGiay, String mucDichSuDung,
                             BigDecimal giaBan, String moTa,
                             String mauSac, String kichThuoc, String chatLieu,
                             String urlAnh1, String urlAnh2, String urlAnh3) {
        this.id = id;
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.tenDanhMuc = tenDanhMuc;
        this.tenThuongHieu = tenThuongHieu;
        this.tenXuatXu = tenXuatXu;
        this.tenDayGiay = tenDayGiay;
        this.tenDeGiay = tenDeGiay;
        this.mucDichSuDung = mucDichSuDung;
        this.giaBan = giaBan;
        this.moTa = moTa;
        this.mauSac = mauSac;
        this.kichThuoc = kichThuoc;
        this.chatLieu = chatLieu;
        this.urlAnh1 = urlAnh1;
        this.urlAnh2 = urlAnh2;
        this.urlAnh3 = urlAnh3;
    }

    // Trả về ảnh đầu tiên để hiển thị ở table/modal
    @JsonProperty("hinhAnhUrl")
    public String getHinhAnhUrl() {
        if (urlAnh1 != null && !urlAnh1.isEmpty()) return urlAnh1;
        if (urlAnh2 != null && !urlAnh2.isEmpty()) return urlAnh2;
        if (urlAnh3 != null && !urlAnh3.isEmpty()) return urlAnh3;
        return null;
    }

}
