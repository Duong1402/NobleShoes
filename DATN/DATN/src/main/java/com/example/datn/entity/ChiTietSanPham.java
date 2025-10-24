package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chi_tiet_san_pham")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    // 🔹 Liên kết bắt buộc với sản phẩm
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_san_pham", nullable = false)
    @JsonIgnoreProperties({"chiTietSanPhams"})
    @JsonBackReference
    private SanPham sanPham;

    // 🔹 Bắt buộc chọn màu sắc
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_mau_sac", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MauSac mauSac;

    // 🔹 Bắt buộc chọn kích thước
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_kich_thuoc", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private KichThuoc kichThuoc;

    // 🔹 Bắt buộc có chất liệu
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chat_lieu", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ChatLieu chatLieu;

    // 🔹 Có thể null nếu không có đợt giảm giá
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dot_giam_gia")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DotGiamGia dotGiamGia;

    // 🔹 Mã biến thể (unique, có thể tự sinh)
    @Column(length = 50, unique = true)
    @Size(max = 50, message = "Mã chi tiết sản phẩm không được vượt quá 50 ký tự")
    private String ma;

    // 🔹 Giá nhập (>= 0)
    @Column(name = "gia_nhap", precision = 18, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true, message = "Giá nhập không được âm")
    private BigDecimal giaNhap;

    // 🔹 Giá bán (>= 0, không được null)
    @Column(name = "gia_ban", precision = 18, scale = 2, nullable = false)
    @NotNull(message = "Giá bán là bắt buộc")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    private BigDecimal giaBan;

    // 🔹 Số lượng tồn (>= 0)
    @Column(name = "so_luong_ton", nullable = false)
    @NotNull(message = "Số lượng tồn là bắt buộc")
    @Min(value = 0, message = "Số lượng tồn không được âm")
    private Integer soLuongTon;

    // 🔹 Ngày tạo (tự set)
    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    // 🔹 Mô tả (không bắt buộc, giới hạn ký tự)
    @Column(name = "mo_ta", length = 100)
    @Size(max = 100, message = "Mô tả không được vượt quá 100 ký tự")
    private String moTa;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDate.now();
        }
    }

    @Transient // Không lưu vào DB
    private String hinhAnhUrl;


}
