// src/main/java/com/example/datn/entity/HoaDonChiTiet.java
package com.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "hoa_don_chi_tiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chi_tiet_san_pham", nullable = false)
    private ChiTietSanPham chiTietSanPham;

    // Lưu lại tên SP “snapshot” phòng khi sau này đổi tên / xóa CTSP
    @Column(name = "ten_san_pham", length = 255)
    private String tenSanPham;

    @Column(name = "size", length = 50)
    private String size;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", nullable = false, precision = 18, scale = 0)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 0)
    private BigDecimal thanhTien;

    /**
     * 1 = bình thường
     * 0 = đã hủy / không tính tồn kho nữa (nếu bạn muốn soft–delete cho dòng chi tiết)
     */
    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai = 1;

    // Tiện: method tính lại thành tiền (gọi trong service trước khi save)
    public void recalcThanhTien() {
        if (donGia != null && soLuong != null) {
            this.thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }
}
