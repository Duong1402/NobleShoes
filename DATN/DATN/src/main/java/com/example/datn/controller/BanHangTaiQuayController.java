package com.example.datn.controller;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.KhachHang;
import com.example.datn.service.BanHangTaiQuayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/ban-hang")
@CrossOrigin(origins = "http://localhost:5173")
public class BanHangTaiQuayController {

    @Autowired
    private BanHangTaiQuayService banHangTaiQuayService;

    // 1. Tạo hóa đơn chờ
    @PostMapping("/tao-hoa-don/{idNhanVien}")
    public ResponseEntity<HoaDon> taoHoaDon(@PathVariable UUID idNhanVien) {
        HoaDon hoaDon = banHangTaiQuayService.taoHoaDonCho(idNhanVien);
        return ResponseEntity.status(HttpStatus.CREATED).body(hoaDon);
    }

    //    Chuyển trạng thái hoaDon khi xoa
    @PutMapping("/huy-hoa-don/{idHoaDon}")
    public ResponseEntity<Void> huyHoaDon(@PathVariable UUID idHoaDon) {
        banHangTaiQuayService.huyHoaDon(idHoaDon);
        return ResponseEntity.noContent().build();
    }

    // 2.Thêm sản phẩm vào hóa đơn
    @PostMapping("/them-san-pham")
    public ResponseEntity<HoaDonChiTiet> themSanPham(
            @RequestParam UUID idHoaDon,
            @RequestParam UUID idChiTietSanPham,
            @RequestParam int soLuong) {
        HoaDonChiTiet hdct = banHangTaiQuayService.themSanPhamVaoHoaDon(idHoaDon, idChiTietSanPham, soLuong);
        return ResponseEntity.ok(hdct);
    }

    //    xóa sản phẩm khói hóa đơn
    @DeleteMapping("/{idHoaDon}/chi-tiet/{idChiTietSanPham}")
    public ResponseEntity<Void> xoaSanPhamKhoiHoaDon(
            @PathVariable UUID idHoaDon,
            @PathVariable UUID idChiTietSanPham) {
        banHangTaiQuayService.xoaSanPhamKhoiHoaDon(idHoaDon, idChiTietSanPham);
        return ResponseEntity.ok().build();
    }


    // 3. Cập nhật khách hàng cho hóa đơn
    @PutMapping("/hoa-don/{idHoaDon}/cap-nhat-khach-hang/{idKhachHang}")
    public ResponseEntity<HoaDon> capNhatKhachHang(@RequestParam UUID idHoaDon, @RequestParam UUID idKhachHang) {
        HoaDon hoaDon = banHangTaiQuayService.capNhatKhachHang(idHoaDon, idKhachHang);
        return ResponseEntity.ok(hoaDon);
    }

    // Tìm kiếm Khách hàng theo SĐT
    @GetMapping("/khach-hang/tim-kiem/{keyword}")
    public ResponseEntity<?> timKhachHang(@PathVariable String keyword) {
        Optional<KhachHang> khachHang = banHangTaiQuayService.timKhachHangByHotenOrSdt(keyword);
        if (khachHang.isPresent()) {
            return ResponseEntity.ok(khachHang.get());
        } else {
            // Trả về 204 No Content hoặc 404 Not Found tùy theo quy ước,
            // nhưng thường trả về 200 kèm body rỗng/null nếu không tìm thấy để Vue.js dễ xử lý.
            return ResponseEntity.ok(null);
        }
    }

    // Thêm nhanh Khách hàng mới
    @PostMapping("/khach-hang/them-nhanh")
    public ResponseEntity<?> themNhanhKhachHang(@RequestBody KhachHang khachHangMoi) {
        try {
            KhachHang khachHangDaThem = banHangTaiQuayService.themKhachHangMoi(khachHangMoi);
            return ResponseEntity.status(HttpStatus.CREATED).body(khachHangDaThem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 4. Áp dụng phiếu giảm giá
    @PutMapping("/hoa-don/{idHoaDon}/giam-gia/{idPhieuGiamGia}")
    public HoaDon apGiamGia(@RequestParam UUID idHoaDon, @RequestParam UUID idPhieuGiamGia) {
        return banHangTaiQuayService.apDungPhieuGiamGia(idHoaDon, idPhieuGiamGia);
    }

    // 5. Thanh toán
    @PostMapping("/thanh-toan")
    public ResponseEntity<HoaDon> thanhToan(
            @RequestParam UUID idHoaDon,
            @RequestParam UUID idPhuongThucThanhToan) {
        HoaDon hoaDon = banHangTaiQuayService.thanhToan(idHoaDon, idPhuongThucThanhToan);
        return ResponseEntity.ok(hoaDon);

    }

    // 6. Lấy chi tiết hóa đơn
    @GetMapping("/{idHoaDon}/chi-tiet")
    public ResponseEntity<List<HoaDonChiTiet>> getChiTiet(@PathVariable UUID idHoaDon) {
        List<HoaDonChiTiet> chiTiet = banHangTaiQuayService.getChiTietHoaDon(idHoaDon);
        return ResponseEntity.ok(chiTiet);
    }
}
