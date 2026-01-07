package com.example.datn.model.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PublicCouponResponse {
    private UUID id;
    private String ma;
    private String ten;
    private Boolean trangThai;
    private Object hinhThucGiamGia;
    private BigDecimal giaTriGiam;
    private BigDecimal giaTriGiamToiDa;
    private BigDecimal giaTriGiamToiThieu;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    public PublicCouponResponse() {}

    public PublicCouponResponse(UUID id, String ma, String ten, Boolean trangThai, Object hinhThucGiamGia,
                                BigDecimal giaTriGiam, BigDecimal giaTriGiamToiDa, BigDecimal giaTriGiamToiThieu,
                                LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        this.id = id;
        this.ma = ma;
        this.ten = ten;
        this.trangThai = trangThai;
        this.hinhThucGiamGia = hinhThucGiamGia;
        this.giaTriGiam = giaTriGiam;
        this.giaTriGiamToiDa = giaTriGiamToiDa;
        this.giaTriGiamToiThieu = giaTriGiamToiThieu;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public Boolean getTrangThai() { return trangThai; }
    public void setTrangThai(Boolean trangThai) { this.trangThai = trangThai; }

    public Object getHinhThucGiamGia() { return hinhThucGiamGia; }
    public void setHinhThucGiamGia(Object hinhThucGiamGia) { this.hinhThucGiamGia = hinhThucGiamGia; }

    public BigDecimal getGiaTriGiam() { return giaTriGiam; }
    public void setGiaTriGiam(BigDecimal giaTriGiam) { this.giaTriGiam = giaTriGiam; }

    public BigDecimal getGiaTriGiamToiDa() { return giaTriGiamToiDa; }
    public void setGiaTriGiamToiDa(BigDecimal giaTriGiamToiDa) { this.giaTriGiamToiDa = giaTriGiamToiDa; }

    public BigDecimal getGiaTriGiamToiThieu() { return giaTriGiamToiThieu; }
    public void setGiaTriGiamToiThieu(BigDecimal giaTriGiamToiThieu) { this.giaTriGiamToiThieu = giaTriGiamToiThieu; }

    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }
}
