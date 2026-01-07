package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "khach_hang")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class KhachHang implements UserDetails {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "ma", unique = true)
    private String ma;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "email")
    private String email;

    @Column(name = "url_anh")
    private String urlAnh;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "ngay_sinh")
    private Date ngaySinh;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "tai_khoan")
    private String taiKhoan;

    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "nguoi_tao", length = 20)
    private String nguoiTao;

    @Column(name = "nguoi_sua", length = 20)
    private String nguoiSua;

    @Column(name = "trang_thai")
    private Byte trangThai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_chuc_vu")
    private ChucVu chucVu;

    @OneToMany(mappedBy = "khachHang", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<DiaChi> danhSachDiaChi = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        // Chỉ set ngày tạo nếu chưa có
        if (this.ngayTao == null) {
            this.ngayTao = new Date();
        }

        if (this.ma == null || this.ma.trim().isEmpty()) {
            this.ma = "TEMP_KH" + UUID.randomUUID().toString().substring(0, 5);
        }

        if (this.trangThai == null) {
            this.trangThai = 1;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.chucVu == null) {
            return List.of();
        }
        // Giả sử ChucVu có getTen()
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.chucVu.getTen().toUpperCase()));
    }@Override
    public String getPassword() {
        // Trả về trường mật khẩu
        return this.matKhau;
    }

    @Override
    public String getUsername() {
        // Trả về trường tên đăng nhập
        return this.taiKhoan;
    }

    @Override
    public boolean isAccountNonExpired() {
        // true = tài khoản không hết hạn
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // true = tài khoản không bị khóa
        // Bạn có thể dùng trường trangThai ở đây
        // Ví dụ: return this.trangThai == 1;
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // true = mật khẩu không hết hạn
        return true;
    }

    @Override
    public boolean isEnabled() {
        // true = tài khoản được kích hoạt
        // Đây là nơi tốt nhất để dùng trangThai
        return this.trangThai != null && this.trangThai == 1;
    }

}
