package com.example.datn.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ChiTietSanPhamDTO {

    private UUID id;
    private String maSP;
    private String tenSP;

    private String tenDanhMuc;
    private String tenThuongHieu;
    private String tenXuatXu;

    private String tenDayGiay;
    private String tenDeGiay;

    /**
     * Field chuẩn (theo bản 2)
     */
    private String tenMucDich;

    /**
     * Giữ tương thích dữ liệu cũ (theo bản 1)
     * Nếu FE/DB đang dùng key mucDichSuDung thì vẫn nhận vào được.
     */
    @JsonAlias({"mucDichSuDung"})
    private String mucDichSuDung;

    private BigDecimal giaBan;
    private String moTa;

    private String mauSac;
    private String kichThuoc;
    private String chatLieu;

    private String urlAnh1;
    private String urlAnh2;
    private String urlAnh3;

    private Integer soLuongTon;

    // ✅ Constructor dùng cho JPQL: new com.example.datn.dto.ChiTietSanPhamDTO(...)
    public ChiTietSanPhamDTO(
            UUID id,
            String maSP,
            String tenSP,
            String tenDanhMuc,
            String tenThuongHieu,
            String tenXuatXu,
            String tenDayGiay,
            String tenDeGiay,
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
        this.tenDayGiay = tenDayGiay;
        this.tenDeGiay = tenDeGiay;
        this.tenMucDich = tenMucDich;
        this.mucDichSuDung = tenMucDich; // ✅ đồng bộ cho FE cũ
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

    // ✅ FE chỉ cần 1 trường ảnh đại diện
    @JsonProperty("hinhAnhUrl")
    public String getHinhAnhUrl() {
        if (urlAnh1 != null && !urlAnh1.isEmpty()) return urlAnh1;
        if (urlAnh2 != null && !urlAnh2.isEmpty()) return urlAnh2;
        if (urlAnh3 != null && !urlAnh3.isEmpty()) return urlAnh3;
        return null;
    }

    // ✅ Nếu nơi nào set mucDichSuDung thì tự đổ qua tenMucDich luôn
    public void setMucDichSuDung(String mucDichSuDung) {
        this.mucDichSuDung = mucDichSuDung;
        if (this.tenMucDich == null || this.tenMucDich.isBlank()) {
            this.tenMucDich = mucDichSuDung;
        }
    }
}
