package com.example.datn.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderPublicResponseDto {
    private String ma;
    private Integer trangThai;
    private LocalDateTime ngayTao;

    private String tenKhachHang;
    private String sdt;
    private String email;
    private String diaChiGiaoHang;

    private String phuongThucThanhToan;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;

    // ✅ FE đang đọc key này
    private List<OrderItemPublicDto> chiTietSanPham;
}
