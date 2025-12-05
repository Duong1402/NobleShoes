// src/main/java/com/example/datn/model/request/TaoHoaDonRequest.java
package com.example.datn.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class TaoHoaDonRequest {

    /* Thông tin chung */
    private String maDon;         // FE gửi lên, nếu null BE tự generate
    private String hoTen;
    private String email;
    private String sdt;
    private String diaChi;
    private String ghiChu;

    /* Tiền */
    private BigDecimal tamTinh;        // tổng tiền hàng
    private BigDecimal phiVanChuyen;   // phí ship
    private BigDecimal giamGia;        // tổng giảm giá (nếu có)
    private BigDecimal tongTien;       // tổng cuối cùng FE tính; nếu 0 BE tự tính

    /* Danh sách sản phẩm */
    private List<Item> items;

    @Data
    public static class Item {
        private UUID chiTietSanPhamId;  // id_ctsp (nếu có)
        private String tenSanPham;      // fallback nếu không map ctsp
        private String size;
        private Integer soLuong;
        private BigDecimal donGia;
        private BigDecimal thanhTien;   // FE tính, BE sẽ kiểm tra lại
    }
}