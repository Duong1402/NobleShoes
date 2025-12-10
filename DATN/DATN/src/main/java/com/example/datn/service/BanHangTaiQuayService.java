package com.example.datn.service;

import com.example.datn.entity.*;
import com.example.datn.expection.LoiPhieuGiamGiaException;
import com.example.datn.model.Response.ThemSanPhamResponse;
import com.example.datn.model.request.ThanhToanRequest;
import com.example.datn.repository.*;
import com.example.datn.service.impl.BanHangTaiQuayServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BanHangTaiQuayService implements BanHangTaiQuayServiceImpl {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final HinhThucThanhToanRepository hinhThucThanhToanRepository;
    private final KhachHangRepository khachHangRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;
    private final NhanVienRepository nhanVienRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public enum trangThaiHoaDon {
        DA_HUY(0),
        CHO_XAC_NHAN(1),
        DA_XAC_NHAN(2),
        DANG_GIAO(4),
        HOAN_THANH(5),
        CHO_THANH_TOAN(3);

        private final int value;

        trangThaiHoaDon(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum trangThaiHoaDonChiTiet {
        DA_XOA(0),          // sản phẩm bị xóa khỏi hóa đơn
        DANG_BAN(1),        // sản phẩm đang được bán (mới thêm vào hóa đơn)
        DA_THANH_TOAN(2),   // sản phẩm đã thanh toán
        DA_TRA_HANG(3);     // khách trả lại hàng

        private final int value;

        trangThaiHoaDonChiTiet(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private BigDecimal tinhTongTien(UUID idHoaDon) {
        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findAllByHoaDonId(idHoaDon);
        return list.stream()
                .filter(ct -> ct.getTrangThai() != trangThaiHoaDonChiTiet.DA_XOA.getValue())
                .map(HoaDonChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public HoaDon taoHoaDonCho(UUID idNhanVien) {

        // ✅ FIX: findByTaiKhoan trả Optional -> phải .orElse(null)
        NhanVien nv = nhanVienRepository.findByTaiKhoan("admin").orElse(null);

        if (nv == null) {
            nv = new NhanVien();
            nv.setTaiKhoan("admin");
            nv.setHoTen("Quản trị viên");
            nv.setEmail("admin@example.com");
            nv = nhanVienRepository.save(nv);
        }

        //        NhanVien nv = nhanVienRepository.findById(idNhanVien)
        //                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        HoaDon hd = new HoaDon();
        hd.setNhanVien(nv);
        hd.setMa(hoaDonRepository.getNextMaHoaDon());
        hd.setLoaiHoaDon("Tại cửa hàng");
        hd.setTrangThai(trangThaiHoaDon.CHO_THANH_TOAN.getValue());
        hd.setNgayTao(LocalDate.now());
        hd.setTongTien(BigDecimal.ZERO);

        HoaDon savedHoaDon = hoaDonRepository.save(hd);

        // --- Ghi lịch sử ngay sau khi tạo hóa đơn ---
        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(savedHoaDon);
        lichSu.setThoiGian(LocalDateTime.now());
        lichSu.setNguoiThucHien("admin"); // mặc định
        lichSu.setGhiChu("Tạo hóa đơn mới");
        lichSu.setTrangThaiMoi(savedHoaDon.getTrangThai());

        lichSuHoaDonRepository.save(lichSu);

        return savedHoaDon;
    }

    @Override
    @Transactional
    public ThemSanPhamResponse themSanPhamVaoHoaDon(UUID idHoaDon, UUID idChiTietSanPham, int soLuong) {
        // 1. Lấy thông tin sản phẩm và GIÁ HIỆN TẠI
        ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm"));

        BigDecimal giaMoi = ctsp.getGiaBan(); // Giá bán hiện tại trong kho

        // Validate tồn kho cơ bản
        if (ctsp.getSoLuongTon() < soLuong) {
            throw new RuntimeException("Số lượng tồn không đủ!");
        }

        // 2. Lấy Hóa Đơn
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        // 3. Tìm xem trong hóa đơn đã có sản phẩm này với ĐÚNG GIÁ NÀY chưa?
        Optional<HoaDonChiTiet> existingHdct = hoaDonChiTietRepository
                .findByIdAndDonGia(idHoaDon, idChiTietSanPham, giaMoi);

        HoaDonChiTiet hdct;
        String warningMessage = null; // Biến lưu thông báo

        if (existingHdct.isPresent()) {
            // === TRƯỜNG HỢP A: Đã có sản phẩm này với giá này -> CỘNG DỒN ===
            hdct = existingHdct.get();
            hdct.setSoLuong(hdct.getSoLuong() + soLuong);
            hdct.setThanhTien(giaMoi.multiply(BigDecimal.valueOf(hdct.getSoLuong())));
        } else {
            // === TRƯỜNG HỢP B: Chưa có sản phẩm này với giá này -> TẠO DÒNG MỚI ===

            // 🔥 LOGIC KIỂM TRA ĐỂ CẢNH BÁO GIÁ CŨ 🔥
            List<HoaDonChiTiet> listCu = hoaDonChiTietRepository
                    .findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham);

            if (!listCu.isEmpty()) {
                BigDecimal giaCu = listCu.get(0).getDonGia();
                String strGiaCu = String.format("%,.0f", giaCu);
                String strGiaMoi = String.format("%,.0f", giaMoi);

                warningMessage = "Sản phẩm đã đổi giá từ " + strGiaCu + " thành " + strGiaMoi;
            }

            // Tạo mới chi tiết hóa đơn
            hdct = new HoaDonChiTiet();
            hdct.setHoaDon(hd);
            hdct.setChiTietSanPham(ctsp);
            hdct.setSoLuong(soLuong);
            hdct.setDonGia(giaMoi); // Lưu giá mới nhất
            hdct.setThanhTien(giaMoi.multiply(BigDecimal.valueOf(soLuong)));
            hdct.setTrangThai(trangThaiHoaDonChiTiet.DANG_BAN.getValue());
        }

        // 4. Lưu và Đẩy ngay xuống DB
        hoaDonChiTietRepository.saveAndFlush(hdct);

        // 5. Cập nhật tồn kho
        ctsp.setSoLuongTon(ctsp.getSoLuongTon() - soLuong);
        chiTietSanPhamRepository.save(ctsp);

        // 6. Tính lại tiền
        capNhatLaiTongTienVaKhuyenMai(idHoaDon);

        // 7. Trả về DTO
        return new ThemSanPhamResponse(hdct, warningMessage);
    }

    @Override
    public HoaDon capNhatKhachHang(UUID idHoaDon, UUID idKhachHang) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        KhachHang kh = khachHangRepository.findById(idKhachHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        hd.setKhachHang(kh);

        hd.setSdt(kh.getSdt());
        hd.setTenKhachHang(kh.getHoTen());

        return hoaDonRepository.save(hd);
    }

    @Override
    public HoaDon apDungPhieuGiamGia(UUID idHoaDon, UUID idPhieuGiamGia) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idPhieuGiamGia)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));

        hd.setPhieuGiamGia(pgg);
        hoaDonRepository.save(hd);

        capNhatLaiTongTienVaKhuyenMai(idHoaDon);

        return hd;
    }

    @Override
    @Transactional(noRollbackFor = {LoiPhieuGiamGiaException.class})
    public HoaDon thanhToan(UUID idHoaDon, ThanhToanRequest request) {

        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        PhieuGiamGia pgg = hd.getPhieuGiamGia();

        if (pgg != null) {
            boolean isLoi = false;
            String lyDo = "";
            Date now = new Date();

            Optional<PhieuGiamGiaCaNhan> pggCaNhanOpt = Optional.empty();

            if (hd.getKhachHang() != null) {
                pggCaNhanOpt = phieuGiamGiaCaNhanRepository.findByKhachHangAndPhieuGiamGia(
                        hd.getKhachHang(),
                        pgg
                );
            }

            if (pggCaNhanOpt.isPresent()) {
                PhieuGiamGiaCaNhan pggCaNhan = pggCaNhanOpt.get();

                if (Boolean.FALSE.equals(pggCaNhan.getTrangThai())) {
                    isLoi = true;
                    lyDo = "Phiếu giảm giá cá nhân của bạn đã bị vô hiệu hóa.";
                } else if (pggCaNhan.getNgayHetHan() != null && now.after(pggCaNhan.getNgayHetHan())) {
                    isLoi = true;
                    lyDo = "Phiếu giảm giá cá nhân của bạn đã hết hạn sử dụng.";
                }

            } else {
                if (Boolean.FALSE.equals(pgg.getTrangThai())) {
                    isLoi = true;
                    lyDo = "Chương trình khuyến mãi này đã kết thúc.";
                } else if (pgg.getNgayKetThuc() != null && now.after(pgg.getNgayKetThuc())) {
                    isLoi = true;
                    lyDo = "Phiếu giảm giá đã hết hạn chương trình.";
                } else if (pgg.getNgayBatDau() != null && now.before(pgg.getNgayBatDau())) {
                    isLoi = true;
                    lyDo = "Chương trình khuyến mãi chưa bắt đầu.";
                }
            }

            if (isLoi) {
                hd.setPhieuGiamGia(null);

                hoaDonRepository.saveAndFlush(hd);

                capNhatLaiTongTienVaKhuyenMai(idHoaDon);

                throw new LoiPhieuGiamGiaException(lyDo + " Vui lòng kiểm tra lại đơn hàng!");
            }
        }

        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

        BigDecimal tongTienHang = tinhTongTien(idHoaDon);
        BigDecimal phiShip = BigDecimal.ZERO;

        hd.setLoaiHoaDon(request.getLoaiHoaDon());

        if ("Online".equalsIgnoreCase(request.getLoaiHoaDon())) {
            hd.setTenKhachHang(request.getTenKhachHang());
            hd.setSdt(request.getSdt());
            hd.setDiaChiGiaoHang(request.getDiaChiGiaoHang());

            phiShip = request.getDiaChiGiaoHang() != null ? request.getPhiVanChuyen() : BigDecimal.ZERO;
            hd.setPhiVanChuyen(phiShip);

            hd.setTrangThai(trangThaiHoaDon.HOAN_THANH.getValue());

        } else {
            hd.setPhiVanChuyen(BigDecimal.ZERO);
            hd.setTenKhachHang(null);
            hd.setSdt(null);
            hd.setTrangThai(trangThaiHoaDon.HOAN_THANH.getValue());
        }

        BigDecimal tongThanhToan = tongTienHang.add(phiShip);

        hd.setTongTien(tongTienHang);
        hd.setTongTienSauGiam(tongThanhToan);

        HinhThucThanhToan httt = new HinhThucThanhToan();
        httt.setHoaDon(hd);
        httt.setPhuongThucThanhToan(pttt);
        httt.setSoTien(tongThanhToan);
        httt.setGhiChu(request.getLoaiHoaDon());
        hinhThucThanhToanRepository.save(httt);

        hd.setNgaySua(LocalDate.now());
        hd.setNguoiSua("nhân viên quầy");

        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findAllByHoaDonId(idHoaDon);
        for (HoaDonChiTiet hdct : list) {
            hdct.setTrangThai(trangThaiHoaDonChiTiet.DA_THANH_TOAN.getValue());
        }
        hoaDonChiTietRepository.saveAll(list);

        return hoaDonRepository.save(hd);
    }

    @Override
    public List<HoaDonChiTiet> getChiTietHoaDon(UUID idHoaDon) {
        return hoaDonChiTietRepository.findAllByHoaDonId(idHoaDon);
    }

    @Override
    @Transactional
    public void huyHoaDon(UUID idHoaDon) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn để hủy"));

        hd.setTrangThai(trangThaiHoaDon.DA_HUY.getValue()); // 0 = Đã hủy
        hoaDonRepository.save(hd);
    }

    @Override
    @Transactional
    public void xoaSanPhamKhoiHoaDon(UUID idHoaDon, UUID idChiTietSanPham) {
        System.out.println("🔍 Xóa sản phẩm trong hóa đơn:");
        System.out.println("   ➜ idHoaDon = " + idHoaDon);
        System.out.println("   ➜ idChiTietSanPham = " + idChiTietSanPham);

        List<HoaDonChiTiet> listHdct = hoaDonChiTietRepository
                .findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham);

        if (listHdct.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy sản phẩm trong hóa đơn!");
            return;
        }

        for (HoaDonChiTiet hdct : listHdct) {
            if (hdct.getTrangThai() != trangThaiHoaDonChiTiet.DA_XOA.getValue()) {

                System.out.println("✅ Tìm thấy chi tiết hóa đơn ID: " + hdct.getId() + ", SL = " + hdct.getSoLuong() + ", Giá = " + hdct.getDonGia());

                ChiTietSanPham ctsp = hdct.getChiTietSanPham();
                ctsp.setSoLuongTon(ctsp.getSoLuongTon() + hdct.getSoLuong());
                chiTietSanPhamRepository.save(ctsp);

                System.out.println("🔄 Hoàn lại tồn cho sản phẩm: " + ctsp.getId() + ", tồn mới = " + ctsp.getSoLuongTon());

                hdct.setTrangThai(trangThaiHoaDonChiTiet.DA_XOA.getValue());

                hoaDonChiTietRepository.saveAndFlush(hdct);
            }
        }

        capNhatLaiTongTienVaKhuyenMai(idHoaDon);
    }

    public List<KhachHang> timKhachHangByHotenOrSdt(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("🔍 Từ khóa tìm kiếm rỗng. Trả về danh sách rỗng.");
            return Collections.emptyList();
        }

        String searchKeyword = keyword.trim();
        System.out.println("🔍 Tìm kiếm DANH SÁCH Khách hàng theo Tên hoặc SĐT: " + searchKeyword);

        List<KhachHang> khachHangList = khachHangRepository.timDanhSachKhachHang(searchKeyword);

        if (!khachHangList.isEmpty()) {
            System.out.println("✅ Tìm thấy " + khachHangList.size() + " Khách hàng.");
        } else {
            System.out.println("⚠️ Không tìm thấy Khách hàng với từ khóa: " + searchKeyword);
        }

        return khachHangList;
    }

    @Transactional
    public KhachHang themKhachHangMoi(KhachHang khachHangMoi) {
        String sdt = khachHangMoi.getSdt();
        if (sdt != null) {
            sdt = sdt.trim();
        }

        khachHangMoi.setSdt(sdt);

        if (khachHangMoi.getHoTen() == null || khachHangMoi.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }

        if (sdt == null || sdt.isEmpty()) {
            throw new IllegalArgumentException("SĐT khách hàng không được để trống.");
        }

        if (khachHangRepository.findBySdt(sdt).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        if (khachHangMoi.getNgayTao() == null) {
            khachHangMoi.setNgayTao(new Date());
        }

        if (khachHangMoi.getMa() == null || khachHangMoi.getMa().trim().isEmpty()) {
            String newMa = generateNewMaKhachHang();
            khachHangMoi.setMa(newMa);
            System.out.println("DEBUG: Mã đã sinh: " + newMa);
        }
        if (khachHangMoi.getTrangThai() == null) {
            khachHangMoi.setTrangThai((byte) 1);
        }

        System.out.println("DEBUG: Mã cuối cùng trước khi lưu: " + khachHangMoi.getMa());
        return khachHangRepository.save(khachHangMoi);
    }

    private String generateNewMaKhachHang() {
        String maxMa = khachHangRepository.findMaxMaKhachHang();
        System.out.println("DEBUG: Max Mã từ DB: " + maxMa);

        int currentNumber = 0;

        if (maxMa != null && maxMa.startsWith("KH")) {
            try {
                String numberPart = maxMa.substring(2);
                currentNumber = Integer.parseInt(numberPart);
            } catch (NumberFormatException e) {
                currentNumber = 0;
            }
        }

        int newNumber = currentNumber + 1;
        return "KH" + String.format("%02d", newNumber);
    }

    public PhieuGiamGia timPhieuGiamGiaTotNhat(UUID idHoaDon, UUID idKhachHang) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).orElseThrow();

        BigDecimal tongTienHang = BigDecimal.ZERO;
        if (hoaDon.getHoaDonChiTiets() != null) {
            for (HoaDonChiTiet ct : hoaDon.getHoaDonChiTiets()) {
                if (ct.getThanhTien() != null) tongTienHang = tongTienHang.add(ct.getThanhTien());
            }
        }
        System.out.println("🔥 [DEBUG] Tổng tiền đơn hàng: " + tongTienHang);

        List<PhieuGiamGia> listCoupons = phieuGiamGiaRepository.findValidCouponsForCustomer(idKhachHang);
        System.out.println("🔥 [DEBUG] Tìm thấy " + listCoupons.size() + " phiếu trong DB (thỏa mãn ngày & trạng thái).");

        PhieuGiamGia bestCoupon = null;
        BigDecimal maxDiscountAmount = BigDecimal.ZERO;

        for (PhieuGiamGia phieu : listCoupons) {
            System.out.println("  👉 Đang check phiếu: " + phieu.getTen() + " | Mã: " + phieu.getMa());

            BigDecimal dieuKienToiThieu = phieu.getGiaTriGiamToiThieu();
            if (dieuKienToiThieu != null && tongTienHang.compareTo(dieuKienToiThieu) < 0) {
                System.out.println("     ❌ BỊ LOẠI: Tổng tiền (" + tongTienHang + ") nhỏ hơn điều kiện tối thiểu (" + dieuKienToiThieu + ")");
                continue;
            }

            BigDecimal currentDiscountAmount = BigDecimal.ZERO;
            if (Boolean.TRUE.equals(phieu.getHinhThucGiamGia())) {
                BigDecimal phanTram = phieu.getGiaTriGiam();
                currentDiscountAmount = tongTienHang.multiply(phanTram).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

                if (phieu.getGiaTriGiamToiDa() != null && currentDiscountAmount.compareTo(phieu.getGiaTriGiamToiDa()) > 0) {
                    currentDiscountAmount = phieu.getGiaTriGiamToiDa();
                    System.out.println("     ℹ️ Áp dụng giảm tối đa: " + currentDiscountAmount);
                }
            } else {
                currentDiscountAmount = phieu.getGiaTriGiam();
            }

            System.out.println("     ✅ HỢP LỆ. Mức giảm: " + currentDiscountAmount);

            if (currentDiscountAmount.compareTo(maxDiscountAmount) > 0) {
                maxDiscountAmount = currentDiscountAmount;
                bestCoupon = phieu;
                System.out.println("     ⭐️ Đây đang là phiếu ngon nhất!");
            }
        }

        return bestCoupon;
    }

    private void capNhatLaiTongTienVaKhuyenMai(UUID idHoaDon) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        PhieuGiamGia phieu = hd.getPhieuGiamGia();

        entityManager.detach(hd);

        BigDecimal tongTienHang = hoaDonChiTietRepository.tongTienHoaDon(idHoaDon);

        BigDecimal soTienGiam = BigDecimal.ZERO;

        if (phieu != null) {
            if (phieu.getGiaTriGiamToiThieu() != null
                    && tongTienHang.compareTo(phieu.getGiaTriGiamToiThieu()) < 0) {
                phieu = null;
            } else {
                if (Boolean.TRUE.equals(phieu.getHinhThucGiamGia())) {
                    BigDecimal phanTram = phieu.getGiaTriGiam();
                    soTienGiam = tongTienHang.multiply(phanTram)
                            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
                    if (phieu.getGiaTriGiamToiDa() != null
                            && soTienGiam.compareTo(phieu.getGiaTriGiamToiDa()) > 0) {
                        soTienGiam = phieu.getGiaTriGiamToiDa();
                    }
                } else {
                    soTienGiam = phieu.getGiaTriGiam();
                }

                if (soTienGiam.compareTo(tongTienHang) > 0) {
                    soTienGiam = tongTienHang;
                }
            }
        }

        BigDecimal tienSauGiam = tongTienHang.subtract(soTienGiam).max(BigDecimal.ZERO);

        hoaDonRepository.updateTienVaKhuyenMai(idHoaDon, tongTienHang, tienSauGiam, phieu);
    }
}
