package com.example.datn.model.Response;

import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.entity.HinhAnh;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.SanPham;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class HoaDonChiTietResponse {

    // ====== BẢN MỚI (snapshot / id) ======
    private UUID id;
    private UUID chiTietSanPhamId;

    // ====== THÔNG TIN CHUNG ======
    private String maSanPhamChiTiet; // ctsp.ma
    private String maSanPham;        // sp.ma
    private String tenSanPham;       // ưu tiên snapshot, fallback sp.ten

    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;

    private String size;       // ưu tiên snapshot, fallback kichThuoc.ten
    private String mauSac;     // mauSac.ten
    private String kichThuoc;  // kichThuoc.ten
    private String tenXuatXu;  // xuatXu.ten

    // ====== ẢNH ======
    // FE mới cần urlAnh1, FE cũ cần hinhAnhUrl -> mình giữ cả 2
    private String urlAnh1;
    private String hinhAnhUrl;

    public HoaDonChiTietResponse() {}

    public HoaDonChiTietResponse(HoaDonChiTiet ct) {
        if (ct == null) return;

        this.id = ct.getId();
        this.soLuong = ct.getSoLuong();
        this.donGia = ct.getDonGia();
        this.thanhTien = ct.getThanhTien();

        // snapshot (nếu bạn có cột tenSanPham/size trong HoaDonChiTiet)
        try { this.tenSanPham = ct.getTenSanPham(); } catch (Exception ignore) {}
        try { this.size = ct.getSize(); } catch (Exception ignore) {}

        ChiTietSanPham ctsp = ct.getChiTietSanPham();
        if (ctsp != null) {
            this.chiTietSanPhamId = ctsp.getId();
            this.maSanPhamChiTiet = ctsp.getMa();

            if (ctsp.getMauSac() != null) {
                this.mauSac = ctsp.getMauSac().getTen();
            }
            if (ctsp.getKichThuoc() != null) {
                this.kichThuoc = ctsp.getKichThuoc().getTen();
                if (this.size == null || this.size.isBlank()) {
                    this.size = this.kichThuoc; // fallback size
                }
            }

            SanPham sp = ctsp.getSanPham();
            if (sp != null) {
                this.maSanPham = sp.getMa();
                if (this.tenSanPham == null || this.tenSanPham.isBlank()) {
                    this.tenSanPham = sp.getTen();
                }

                if (sp.getXuatXu() != null) {
                    this.tenXuatXu = sp.getXuatXu().getTen();
                }else {
                    this.tenXuatXu = "";
                }

                // ảnh từ SanPham.hinhAnh
                HinhAnh ha = sp.getHinhAnh();
                if (ha != null) {
                    String u = ha.getUrlAnh1();
                    if (u != null && !u.isBlank()) {
                        this.hinhAnhUrl = u;
                        if (this.urlAnh1 == null || this.urlAnh1.isBlank()) {
                            this.urlAnh1 = u;
                        }
                    }
                }
            }
        }

        if (this.hinhAnhUrl == null || this.hinhAnhUrl.isBlank()) {
            this.hinhAnhUrl = this.urlAnh1;
        } else if (this.urlAnh1 == null || this.urlAnh1.isBlank()) {
            this.urlAnh1 = this.hinhAnhUrl;
        }
    }
}