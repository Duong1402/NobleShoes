package com.example.datn.dto;

import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.entity.HinhAnh;
import com.example.datn.entity.SanPham;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPhamResponse {
    private UUID id;
    private String ma;
    private String tenSanPham;
    private String mauSac;
    private String kichThuoc;
    private BigDecimal giaBan;
    private Integer soLuongTon;
    private String tenXuatXu;

    private String urlAnh1;
    private String urlAnh2;
    private String urlAnh3;

    public ChiTietSanPhamResponse(ChiTietSanPham ctsp) {
        this.id = ctsp.getId();
        this.ma = ctsp.getMa();
        this.soLuongTon = ctsp.getSoLuongTon();
        this.giaBan = ctsp.getGiaBan();

        // 1. Map Màu sắc
        if (ctsp.getMauSac() != null) {
            this.mauSac = ctsp.getMauSac().getTen();
        }

        // 2. Map Kích thước (Đây là cái bạn đang thiếu)
        if (ctsp.getKichThuoc() != null) {
            this.kichThuoc = ctsp.getKichThuoc().getTen();
        }

        // 3. Map thông tin từ Sản phẩm cha
        SanPham sp = ctsp.getSanPham();
        if (sp != null) {
            this.tenSanPham = sp.getTen();

            // Map Xuất xứ (Đây là cái bạn đang thiếu)
            if (sp.getXuatXu() != null) {
                this.tenXuatXu = sp.getXuatXu().getTen();
            }

            // Map Hình ảnh
            HinhAnh ha = sp.getHinhAnh();
            if (ha != null) {
                this.urlAnh1 = ha.getUrlAnh1();
                this.urlAnh2 = ha.getUrlAnh2();
                this.urlAnh3 = ha.getUrlAnh3();
            }
        }
    }

    @JsonProperty("hinhAnhUrl")
    public String getHinhAnhUrl() {
        if (urlAnh1 != null && !urlAnh1.isEmpty()) return urlAnh1;
        if (urlAnh2 != null && !urlAnh2.isEmpty()) return urlAnh2;
        if (urlAnh3 != null && !urlAnh3.isEmpty()) return urlAnh3;
        return null;
    }
}
