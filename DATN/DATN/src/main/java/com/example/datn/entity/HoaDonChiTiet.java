package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private HoaDon hoaDon;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chi_tiet_san_pham", nullable = false)
    private ChiTietSanPham chiTietSanPham;

    // ✅ Snapshot tên SP (nếu DB có cột thì dùng, không có cũng không ép)
    @Column(name = "ten_san_pham", length = 255)
    private String tenSanPham;

    // ✅ Snapshot size (nếu DB có cột thì dùng)
    @Column(name = "size", length = 50)
    private String size;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    /**
     * 1 = bình thường
     * 0 = đã hủy / không tính tồn kho nữa (soft)
     */
    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai = 1;

    // ✅ Tiện: tính lại thành tiền
    public void recalcThanhTien() {
        if (donGia != null && soLuong != null) {
            this.thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }
    }
}
