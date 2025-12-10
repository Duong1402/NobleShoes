package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class NhanVien implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "ma", nullable = false, unique = true, length = 50)
    private String ma;

    @Column(name = "ho_ten", nullable = false, length = 200)
    private String hoTen;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "email", unique = true, length = 200)
    private String email;

    @Column(name = "url_anh", length = 100)
    private String urlAnh;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "ngay_sinh")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngaySinh;

    @Column(name = "dia_chi", length = 100)
    private String diaChi;

    @Column(name = "cccd", length = 20)
    private String cccd;

    @Column(name = "tai_khoan", unique = true, length = 100)
    private String taiKhoan;

    @Column(name = "mat_khau", length = 200)
    private String matKhau;

    @Column(name = "nguoi_tao", length = 20)
    private String nguoiTao;

    @Column(name = "nguoi_sua", length = 20)
    private String nguoiSua;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_sua")
    private LocalDate ngaySua;

    @Column(name = "trang_thai")
    private Byte trangThai = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_chuc_vu")
    private ChucVu chucVu;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDate.now();
        if (this.trangThai == null) this.trangThai = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        this.ngaySua = LocalDate.now();
    }

    /**
     * ✅ Dùng chucVu.ma để set ROLE cho chuẩn (tránh ten có dấu/khoảng trắng)
     * CV01 = ADMIN
     * CV02/CV03 = EMPLOYEE
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.chucVu == null || this.chucVu.getMa() == null) {
            return List.of();
        }

        String maCv = this.chucVu.getMa().trim().toUpperCase();
        String role;
        switch (maCv) {
            case "CV01" -> role = "ROLE_ADMIN";
            case "CV02", "CV03" -> role = "ROLE_EMPLOYEE";
            default -> role = "ROLE_EMPLOYEE";
        }

        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override public String getPassword() { return this.matKhau; }
    @Override public String getUsername() { return this.taiKhoan; }

    @Override public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return this.trangThai != null && this.trangThai == 1;
    }

    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return this.trangThai != null && this.trangThai == 1;
    }
}
