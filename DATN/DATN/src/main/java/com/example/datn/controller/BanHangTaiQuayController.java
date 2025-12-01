package com.example.datn.controller;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.PhieuGiamGia;
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
    public ResponseEntity<HoaDon> xoaSanPhamKhoiHoaDon(
            @PathVariable UUID idHoaDon,
            @PathVariable UUID idChiTietSanPham) {
        // Service xóa SP và tự động tính tiền
        banHangTaiQuayService.xoaSanPhamKhoiHoaDon(idHoaDon, idChiTietSanPham);

        // Lấy hóa đơn mới nhất
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
            // Gọi Service với tham số mới
            HoaDon hoaDonDaThanhToan = banHangTaiQuayService.thanhToan(
                    idHoaDon,
                    request
            );
            return ResponseEntity.ok(hoaDonDaThanhToan);

        } catch (Exception e) {
            // Trả về lỗi để Frontend hiển thị notify.error
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
        // 1. Lấy hóa đơn
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        if (hd.getKhachHang() == null) {
            return ResponseEntity.badRequest().body("Vui lòng chọn khách hàng trước.");
        }

        // 🔥 BƯỚC 1 (QUAN TRỌNG NHẤT): TÍNH LẠI TỔNG TIỀN TỪ CHI TIẾT SẢN PHẨM
        // Lý do: DB có thể đang lưu tong_tien = 0 nếu chưa update kịp
        BigDecimal tongTienHang = BigDecimal.ZERO;

        // Cách 1: Nếu Entity HoaDon đã map List<HoaDonChiTiet>
        if (hd.getHoaDonChiTiets() != null) {
            for (HoaDonChiTiet ct : hd.getHoaDonChiTiets()) {
                if (ct.getThanhTien() != null) {
                    tongTienHang = tongTienHang.add(ct.getThanhTien());
                }
            }
        }

        // Cập nhật lại tổng tiền chuẩn vào hóa đơn
        hd.setTongTien(tongTienHang);

        // 🔥 BƯỚC 2: TÌM PHIẾU GIẢM GIÁ TỐT NHẤT
        PhieuGiamGia bestCoupon = banHangTaiQuayService.timPhieuGiamGiaTotNhat(idHoaDon, hd.getKhachHang().getId());

        if (bestCoupon != null) {
            hd.setPhieuGiamGia(bestCoupon);

            BigDecimal soTienGiam = BigDecimal.ZERO;

            // Logic tính toán: TRUE = %, FALSE = Tiền mặt (Khớp với ảnh DB của bạn)
            if (Boolean.TRUE.equals(bestCoupon.getHinhThucGiamGia())) {
                // --- TRƯỜNG HỢP GIẢM THEO % ---
                BigDecimal phanTram = bestCoupon.getGiaTriGiam(); // Ví dụ: 10

                // Công thức: Tổng * % / 100
                soTienGiam = tongTienHang.multiply(phanTram)
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

                // Check giảm tối đa
                if (bestCoupon.getGiaTriGiamToiDa() != null
                        && soTienGiam.compareTo(bestCoupon.getGiaTriGiamToiDa()) > 0) {
                    soTienGiam = bestCoupon.getGiaTriGiamToiDa();
                }
            } else {
                // --- TRƯỜNG HỢP GIẢM TIỀN MẶT ---
                soTienGiam = bestCoupon.getGiaTriGiam(); // Ví dụ: 20000
            }

            // Chặn giảm âm tiền (Không được giảm quá tổng tiền hàng)
            if (soTienGiam.compareTo(tongTienHang) > 0) {
                soTienGiam = tongTienHang;
            }

            // Cập nhật tổng tiền sau giảm
            BigDecimal tienSauGiam = tongTienHang.subtract(soTienGiam);
            hd.setTongTienSauGiam(tienSauGiam);

            // Lưu vào DB
            hoaDonRepository.save(hd);

            System.out.println("✅ Đã áp mã: " + bestCoupon.getTen() + " | Tổng: " + tongTienHang + " | Giảm: " + soTienGiam);

            // Trả về Object để FE hiển thị
            return ResponseEntity.ok(hd);
        } else {
            // Không tìm thấy mã -> Reset về giá gốc
            hd.setPhieuGiamGia(null);
            hd.setTongTienSauGiam(tongTienHang); // Trả về bằng tổng tiền hàng
            hoaDonRepository.save(hd);

            System.out.println("⚠️ Không có mã phù hợp. Reset về: " + tongTienHang);

            // Vẫn trả về OK kèm Object hóa đơn để FE cập nhật lại giá gốc
            return ResponseEntity.ok(hd);
        }
    }

}
