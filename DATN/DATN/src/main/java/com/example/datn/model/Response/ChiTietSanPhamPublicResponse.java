package com.example.datn.model.Response;

import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.entity.HinhAnh;
import com.example.datn.entity.SanPham;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ChiTietSanPhamPublicResponse {

    private UUID id;

    private String maSP;              // mã sản phẩm
    private String tenSP;             // tên sản phẩm
    private String tenDanhMuc;        // danh mục
    private String tenThuongHieu;     // thương hiệu
    private String tenMucDichSuDung;  // mục đích sử dụng
    private String tenXuatXu;         // xuất xứ

    private BigDecimal giaBan;
    private Integer soLuongTon;

    private String moTa;

    private String mauSac;
    private String kichThuoc;
    private String chatLieu;

    // ảnh
    private String urlAnh1;
    private String urlAnh2;
    private String urlAnh3;

    // alias cho FE: dùng ảnh 1 làm ảnh chính
    private String hinhAnhUrl;

    public ChiTietSanPhamPublicResponse(ChiTietSanPham ct) {
        this.id = ct.getId();
        this.giaBan = ct.getGiaBan();
        this.soLuongTon = ct.getSoLuongTon();

        // ====== thông tin SP cha ======
        SanPham sp = ct.getSanPham();
        if (sp != null) {
            this.maSP = sp.getMa();
            this.tenSP = sp.getTen();

            if (sp.getDanhMuc() != null) {
                this.tenDanhMuc = sp.getDanhMuc().getTen();
            }
            if (sp.getThuongHieu() != null) {
                this.tenThuongHieu = sp.getThuongHieu().getTen();
            }
            if (sp.getMucDichSuDung() != null) {
                this.tenMucDichSuDung = sp.getMucDichSuDung().getTen();
            }
            if (sp.getXuatXu() != null) {
                this.tenXuatXu = sp.getXuatXu().getTen();
            }

            // Mô tả: cột thường nằm ở ChiTietSanPham, nên lấy từ ct
            this.moTa = ct.getMoTa();

            // ====== ảnh ======
            HinhAnh ha = sp.getHinhAnh();
            if (ha != null) {
                this.urlAnh1 = ha.getUrlAnh1();
                this.urlAnh2 = ha.getUrlAnh2();
                this.urlAnh3 = ha.getUrlAnh3();
                this.hinhAnhUrl = ha.getUrlAnh1(); // ảnh chính
            }
        } else {
            // phòng null
            this.moTa = ct.getMoTa();
        }

        // ====== thuộc tính chi tiết (màu, size, chất liệu) ======
        if (ct.getMauSac() != null) {
            this.mauSac = ct.getMauSac().getTen();
        }
        if (ct.getKichThuoc() != null) {
            this.kichThuoc = ct.getKichThuoc().getTen();
        }
        if (ct.getChatLieu() != null) {
            this.chatLieu = ct.getChatLieu().getTen();
        }
    }
}
