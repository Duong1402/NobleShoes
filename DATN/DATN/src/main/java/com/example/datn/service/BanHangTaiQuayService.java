package com.example.datn.service;

import com.example.datn.entity.*;
import com.example.datn.repository.*;
import com.example.datn.service.impl.BanHangTaiQuayServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private final NhanVienRepository nhanVienRepository;

    public enum trangThaiHoaDon {
        DA_HUY(0),
        CHO_XAC_NHAN(1),
        DA_XAC_NHAN(2),
        DANG_GIAO(3),
        HOAN_THANH(4),
        CHO_THANH_TOAN(5);

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

        NhanVien nv = nhanVienRepository.findByTaiKhoan("admin");
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
        hd.setLoaiHoaDon("Tại quầy");
        hd.setTrangThai(trangThaiHoaDon.CHO_THANH_TOAN.getValue());
        hd.setNgayTao(LocalDate.now());
        hd.setTongTien(BigDecimal.ZERO);
        return hoaDonRepository.save(hd);
    }

    @Override
    @Transactional
    public HoaDonChiTiet themSanPhamVaoHoaDon(UUID idHoaDon, UUID idChiTietSanPham, int soLuong) {
        ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm"));
        if (ctsp.getSoLuongTon() < soLuong) {
            throw new RuntimeException("Số lượng tồn không đủ!");
        }

        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        HoaDonChiTiet hdct = hoaDonChiTietRepository.findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham).orElse(null);
        if (hdct != null) {
            hdct.setSoLuong(hdct.getSoLuong() + soLuong);
            hdct.setThanhTien(ctsp.getGiaBan().multiply(BigDecimal.valueOf(hdct.getSoLuong())));
        } else {
            hdct = new HoaDonChiTiet();
            hdct.setHoaDon(hd);
            hdct.setChiTietSanPham(ctsp);
            hdct.setSoLuong(soLuong);
            hdct.setDonGia(ctsp.getGiaBan());
            hdct.setThanhTien(ctsp.getGiaBan().multiply(new java.math.BigDecimal(soLuong)));
            hdct.setTrangThai(trangThaiHoaDonChiTiet.DANG_BAN.getValue());
        }

        hoaDonChiTietRepository.save(hdct);

        // cập nhật tồn kho
        ctsp.setSoLuongTon(ctsp.getSoLuongTon() - soLuong);
        chiTietSanPhamRepository.save(ctsp);

        return hdct;
    }

    @Override
    public HoaDon capNhatKhachHang(UUID idHoaDon, UUID idKhachHang) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        KhachHang kh = khachHangRepository.findById(idKhachHang)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        hd.setKhachHang(kh);
        return hoaDonRepository.save(hd);
    }

    @Override
    public HoaDon apDungPhieuGiamGia(UUID idHoaDon, UUID idPhieuGiamGia) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idPhieuGiamGia)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));
        BigDecimal tongTien = tinhTongTien(idHoaDon);
        BigDecimal giam = pgg.getGiaTriGiam() != null ? pgg.getGiaTriGiam() : BigDecimal.ZERO;
        hd.setTongTien(tongTien);
        hd.setTongTienSauGiam(tongTien.subtract(giam));
        hd.setPhieuGiamGia(pgg);
        return hoaDonRepository.save(hd);
    }

    @Override
    @Transactional
    public HoaDon thanhToan(UUID idHoaDon, UUID idPhuongThucThanhToan) {
        BigDecimal tongTienTinhToan = tinhTongTien(idHoaDon);

        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(idPhuongThucThanhToan)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

        // Tạo bản ghi hình thức thanh toán
        HinhThucThanhToan httt = new HinhThucThanhToan();
        httt.setHoaDon(hd);
        httt.setPhuongThucThanhToan(pttt);
        httt.setSoTien(tongTienTinhToan);
        hinhThucThanhToanRepository.save(httt);

        // Cập nhật hóa đơn
        hd.setTongTien(tongTienTinhToan);
        hd.setTongTienSauGiam(tongTienTinhToan);
        hd.setTrangThai(trangThaiHoaDon.HOAN_THANH.getValue());
        hd.setNgaySua(LocalDate.now());
        hd.setNguoiSua("nhân viên quầy");

        // Cập nhật trạng thái chi tiết
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

        Optional<HoaDonChiTiet> optional = hoaDonChiTietRepository
                .findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham);

        if (optional.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy sản phẩm trong hóa đơn!");
            return;
        }

        HoaDonChiTiet hdct = optional.get();
        System.out.println("✅ Tìm thấy chi tiết hóa đơn, SL = " + hdct.getSoLuong());

        ChiTietSanPham ctsp = hdct.getChiTietSanPham();
        ctsp.setSoLuongTon(ctsp.getSoLuongTon() + hdct.getSoLuong());
        chiTietSanPhamRepository.save(ctsp);

        System.out.println("🔄 Hoàn lại tồn cho sản phẩm: " + ctsp.getId() + ", tồn mới = " + ctsp.getSoLuongTon());

        hdct.setTrangThai(trangThaiHoaDonChiTiet.DA_XOA.getValue());
        hoaDonChiTietRepository.save(hdct);
    }

    public Optional<KhachHang> timKhachHangByHotenOrSdt(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("🔍 Từ khóa tìm kiếm rỗng.");
            return Optional.empty();
        }

        String searchKeyword = keyword.trim();
        System.out.println("🔍 Tìm kiếm Khách hàng theo Tên hoặc SĐT: " + searchKeyword);

        Optional<KhachHang> optionalKhachHang = khachHangRepository.timTheoTenHoacSdt(searchKeyword);

        if (optionalKhachHang.isPresent()) {
            System.out.println("✅ Tìm thấy Khách hàng: " + optionalKhachHang.get().getHoTen());
        } else {
            System.out.println("⚠️ Không tìm thấy Khách hàng với từ khóa: " + searchKeyword);
        }

        return optionalKhachHang;
    }

    @Transactional
    public KhachHang themKhachHangMoi(KhachHang khachHangMoi) {
        if (khachHangMoi.getHoTen() == null || khachHangMoi.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }

        if (khachHangMoi.getSdt() == null || khachHangMoi.getSdt().trim().isEmpty()) {
            throw new IllegalArgumentException("SĐT khách hàng không được để trống.");
        }

        // 1. Kiểm tra SĐT đã tồn tại chưa
        if (khachHangRepository.findBySdt(khachHangMoi.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        if (khachHangMoi.getNgayTao() == null) {
            khachHangMoi.setNgayTao(new Date());
        }

//         Thiết lập các giá trị mặc định (tùy thuộc vào Entity KhachHang của bạn)
        if (khachHangMoi.getMa() == null|| khachHangMoi.getMa().trim().isEmpty()) {
            String newMa = generateNewMaKhachHang();
            khachHangMoi.setMa(newMa); // Tự sinh mã trên Server
            System.out.println("DEBUG: Mã đã sinh: " + newMa);
        }
        if (khachHangMoi.getTrangThai() == null) {
            khachHangMoi.setTrangThai((byte) 1); // Set trạng thái mặc định
        }

        System.out.println("DEBUG: Mã cuối cùng trước khi lưu: " + khachHangMoi.getMa()); // ✅ THÊM LOG NÀY
        return khachHangRepository.save(khachHangMoi);
    }

    private String generateNewMaKhachHang() {
        String maxMa = khachHangRepository.findMaxMaKhachHang(); // Lấy mã lớn nhất: ví dụ "KH015"
        System.out.println("DEBUG: Max Mã từ DB: " + maxMa);

        int currentNumber = 0;

        if (maxMa != null && maxMa.startsWith("KH")) {
            try {
                // Tách phần số từ chuỗi (Ví dụ: "KH015" -> "015" -> 15)
                String numberPart = maxMa.substring(2);
                currentNumber = Integer.parseInt(numberPart);
            } catch (NumberFormatException e) {
                // Trường hợp mã không đúng định dạng "KHXXX", bắt đầu lại từ 0
                currentNumber = 0;
            }
        }

        int newNumber = currentNumber + 1;
        String newMa = "KH" + String.format("%02d", newNumber);

        return newMa;
    }
}
