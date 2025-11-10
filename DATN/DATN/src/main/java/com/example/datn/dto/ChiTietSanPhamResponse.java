package com.example.datn.dto;

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

    @JsonProperty("hinhAnhUrl")
    public String getHinhAnhUrl() {
        if (urlAnh1 != null && !urlAnh1.isEmpty()) return urlAnh1;
        if (urlAnh2 != null && !urlAnh2.isEmpty()) return urlAnh2;
        if (urlAnh3 != null && !urlAnh3.isEmpty()) return urlAnh3;
        return null;
    }
}
