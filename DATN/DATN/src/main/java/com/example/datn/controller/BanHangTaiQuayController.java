package com.example.datn.controller;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.expection.LoiPhieuGiamGiaException;
import com.example.datn.model.Response.ThemSanPhamResponse;
import com.example.datn.model.request.ThanhToanRequest;
import com.example.datn.repository.HoaDonRepository;
import com.example.datn.service.BanHangTaiQuayService;
import com.example.datn.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/ban-hang")
@CrossOrigin(origins = "http://localhost:5173")
public class BanHangTaiQuayController {

    @Autowired
    private BanHangTaiQuayService banHangTaiQuayService;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @GetMapping("/hoa-don/{idHoaDon}")
    public ResponseEntity<HoaDon> getHoaDonById(@PathVariable UUID idHoaDon) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        return ResponseEntity.ok(hd);
    }

    // 1. Tạo hóa đơn chờ
    @PostMapping("/tao-hoa-don")
    public ResponseEntity<HoaDon> taoHoaDon(@RequestParam(name = "idNhanVien", required = false) UUID idNhanVien) {
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
    public ResponseEntity<ThemSanPhamResponse> themSanPham(
            @RequestParam UUID idHoaDon,
            @RequestParam UUID idChiTietSanPham,
            @RequestParam int soLuong) {
        ThemSanPhamResponse response = banHangTaiQuayService.themSanPhamVaoHoaDon(idHoaDon, idChiTietSanPham, soLuong);

        return ResponseEntity.ok(response);
    }

    //    xóa sản phẩm khói hóa đơn
    @DeleteMapping("/{idHoaDon}/chi-tiet/{idChiTietSanPham}")
    public ResponseEntity<HoaDon> xoaSanPhamKhoiHoaDon(
            @PathVariable UUID idHoaDon,
            @PathVariable UUID idChiTietSanPham) {
        banHangTaiQuayService.xoaSanPhamKhoiHoaDon(idHoaDon, idChiTietSanPham);

        HoaDon hd = hoaDonRepository.findById(idHoaDon).orElseThrow();
        return ResponseEntity.ok(hd);
    }


    // 3. Cập nhật khách hàng cho hóa đơn
    @PutMapping("/hoa-don/{idHoaDon}/cap-nhat-khach-hang/{idKhachHang}")
    public ResponseEntity<HoaDon> capNhatKhachHang(@PathVariable UUID idHoaDon, @PathVariable UUID idKhachHang) {
        HoaDon hoaDon = banHangTaiQuayService.capNhatKhachHang(idHoaDon, idKhachHang);
        return ResponseEntity.ok(hoaDon);
    }

    // Tìm kiếm Khách hàng theo SĐT
    @GetMapping("/khach-hang/tim-kiem/{keyword}")
    public ResponseEntity<?> timKhachHang(@PathVariable String keyword) {
        List<KhachHang> khachHang = banHangTaiQuayService.timKhachHangByHotenOrSdt(keyword);
        return ResponseEntity.ok(khachHang);
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
    @PutMapping("/hoa-don/{idHoaDon}/giam-gia/{idPhieuGiamGiaCaNhan}")
    public ResponseEntity<HoaDon> apGiamGia(@PathVariable UUID idHoaDon, @PathVariable("idPhieuGiamGiaCaNhan") UUID idPhieuGiamGiaCaNhan) {
        HoaDon hd = banHangTaiQuayService.apDungPhieuGiamGia(idHoaDon, idPhieuGiamGiaCaNhan);
        return ResponseEntity.ok(hd);
    }

    // 5. Thanh toán
    @PostMapping("/hoa-don/{idHoaDon}/thanh-toan")
    public ResponseEntity<?> thanhToan(
            @PathVariable UUID idHoaDon,
            @RequestBody ThanhToanRequest request
    ) {
        try {
            HoaDon hoaDonDaThanhToan = banHangTaiQuayService.thanhToan(
                    idHoaDon,
                    request
            );
            return ResponseEntity.ok(hoaDonDaThanhToan);

        } catch (LoiPhieuGiamGiaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 6. Lấy chi tiết hóa đơn
    @GetMapping("/{idHoaDon}/chi-tiet")
    public ResponseEntity<List<HoaDonChiTiet>> getChiTiet(@PathVariable UUID idHoaDon) {
        List<HoaDonChiTiet> chiTiet = banHangTaiQuayService.getChiTietHoaDon(idHoaDon);
        return ResponseEntity.ok(chiTiet);
    }

    @PostMapping("/ap-dung-khuyen-mai-tu-dong/{idHoaDon}")
    public ResponseEntity<?> autoApplyBestCoupon(@PathVariable UUID idHoaDon) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        if (hd.getKhachHang() == null) {
            return ResponseEntity.badRequest().body("Vui lòng chọn khách hàng trước.");
        }

        PhieuGiamGia bestCoupon = banHangTaiQuayService.timPhieuGiamGiaTotNhat(idHoaDon, hd.getKhachHang().getId());

        if (bestCoupon != null) {
            hd.setPhieuGiamGia(bestCoupon);
            System.out.println("✅ Tìm thấy mã tốt nhất: " + bestCoupon.getTen());
        } else {
            hd.setPhieuGiamGia(null);
            System.out.println("⚠️ Không tìm thấy mã phù hợp -> Xóa mã cũ.");
        }

        hoaDonRepository.save(hd);

        banHangTaiQuayService.capNhatLaiTongTienVaKhuyenMai(hd);

        return ResponseEntity.ok(hd);
    }

    @PutMapping("/hoa-don/{idHoaDon}")
    public ResponseEntity<?> capNhatThongTinHoaDon(
            @PathVariable UUID idHoaDon,
            @RequestBody ThanhToanRequest request) {

        System.out.println("========================================");
        System.out.println("LOG CONTROLLER - ID: " + idHoaDon);
        System.out.println("1. Request Ship (Raw): " + request.getPhiVanChuyen());
        System.out.println("2. Request Tổng Tiền (Raw): " + request.getTongTienSauGiam());
        System.out.println("3. Request Địa chỉ: " + request.getDiaChiGiaoHang());
        System.out.println("========================================");

        try {
            HoaDon hd = banHangTaiQuayService.capNhatThongTinHoaDon(idHoaDon, request);
            return ResponseEntity.ok(hd);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
