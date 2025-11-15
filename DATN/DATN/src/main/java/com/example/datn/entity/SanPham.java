package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "san_pham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "ma", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Mã sản phẩm không được để trống")
    @Size(max = 50, message = "Mã sản phẩm không được vượt quá 50 ký tự")
    private String ma;

    @Column(name = "ten", columnDefinition = "nvarchar(300)", nullable = false)
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 300, message = "Tên sản phẩm không được vượt quá 300 ký tự")
    private String ten;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDate ngayCapNhat;

    @Column(name = "nguoi_tao", length = 50)
    @Size(max = 50, message = "Tên người tạo không được vượt quá 50 ký tự")
    private String nguoiTao;

    @Column(name = "nguoi_cap_nhat", length = 50)
    @Size(max = 50, message = "Tên người cập nhật không được vượt quá 50 ký tự")
    private String nguoiCapNhat;

    @Column(name = "trang_thai")
    @NotNull(message = "Trạng thái sản phẩm không được để trống")
    private Boolean trangThai;

    // --- Quan hệ bắt buộc ---
//    @ManyToOne(fetch = FetchType.LAZY)
    @OneToOne(cascade = CascadeType.ALL) // tự động lưu HinhAnh khi save SanPham
    @JoinColumn(name = "id_hinh_anh")    // map đúng cột DB
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private HinhAnh hinhAnh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuong_hieu")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @NotNull(message = "Danh mục sản phẩm không được để trống")
    private DanhMuc danhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_xuat_xu")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private XuatXu xuatXu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_de_giay")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DeGiay deGiay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_day_giay")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DayGiay dayGiay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_muc_dich_su_dung")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MucDichSuDung mucDichSuDung;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"sanPham"})
    @JsonManagedReference
    private List<ChiTietSanPham> chiTietSanPhams = new ArrayList<>();

    // --- Tự động set ngày tạo ---
    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDate.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDate.now();
    }
}
