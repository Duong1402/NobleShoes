package com.example.datn.service;

import com.example.datn.dto.VoucherInfo;
import com.example.datn.entity.*;
import com.example.datn.expection.LoiPhieuGiamGiaException;
import com.example.datn.model.Response.HoaDonVoucherCheckResponse;
import com.example.datn.model.Response.ThemSanPhamResponse;
import com.example.datn.model.request.ThanhToanRequest;
import com.example.datn.repository.*;
import com.example.datn.service.impl.BanHangTaiQuayServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


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
    private final DiaChiRepository diaChiRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public enum trangThaiHoaDon {
        CHO_THANH_TOAN(0),
        CHO_XAC_NHAN(1),
        DA_XAC_NHAN(2),
        DANG_CHUAN_BI(3),
        DANG_GIAO(4),
        GIAO_HANG_THAT_BAI(5),
        HOAN_THANH(6),
        DA_HUY(7);

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

        NhanVien nv = null;

        if (idNhanVien != null) {
            nv = nhanVienRepository.findById(idNhanVien).orElse(null);
        }

        if (nv == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String username = auth.getName(); // Lấy username/email từ token
                nv = nhanVienRepository.findByTaiKhoan(username).orElse(null);
            }
        }

        if (nv == null) {
            throw new RuntimeException("Không xác định được nhân viên thực hiện. Vui lòng đăng nhập!");
        }

        HoaDon hd = new HoaDon();
        hd.setNhanVien(nv);
        hd.setMa(hoaDonRepository.getNextMaHoaDon());
        hd.setLoaiHoaDon("Tại cửa hàng");
        hd.setTrangThai(trangThaiHoaDon.CHO_THANH_TOAN.getValue());
        hd.setNgayTao(LocalDate.now());
        hd.setTongTien(BigDecimal.ZERO);

        KhachHang khachLe = khachHangRepository.findByMaIgnoreCase("KH01").orElse(null);

        if (khachLe != null) {
            hd.setKhachHang(khachLe); // Gán mối quan hệ
            hd.setTenKhachHang(khachLe.getHoTen());
            hd.setSdt(khachLe.getSdt());
        }

        HoaDon savedHoaDon = hoaDonRepository.save(hd);

        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(savedHoaDon);
        lichSu.setThoiGian(LocalDateTime.now());
        lichSu.setNguoiThucHien(nv.getHoTen() != null ? nv.getHoTen() : nv.getTaiKhoan());
        lichSu.setGhiChu("Tạo hóa đơn mới");
        lichSu.setTrangThaiMoi(savedHoaDon.getTrangThai());

        lichSuHoaDonRepository.save(lichSu);

        return savedHoaDon;
    }

    @Override
    @Transactional
    public ThemSanPhamResponse themSanPhamVaoHoaDon(UUID idHoaDon, UUID idChiTietSanPham, int soLuong) {

        ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm"));

        BigDecimal giaMoi = ctsp.getGiaBan();

        if (ctsp.getSoLuongTon() < soLuong) {
            throw new RuntimeException("Số lượng tồn không đủ!");
        }

        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        Optional<HoaDonChiTiet> existingHdct = hoaDonChiTietRepository
                .findByIdAndDonGia(idHoaDon, idChiTietSanPham, giaMoi);

        HoaDonChiTiet hdct;
        String warningMessage = null;

        if (existingHdct.isPresent()) {
            hdct = existingHdct.get();
            hdct.setSoLuong(hdct.getSoLuong() + soLuong);
            hdct.setThanhTien(giaMoi.multiply(BigDecimal.valueOf(hdct.getSoLuong())));
        } else {
            List<HoaDonChiTiet> listCu = hoaDonChiTietRepository
                    .findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham);

            if (!listCu.isEmpty()) {
                BigDecimal giaCuTrongHD = listCu.get(0).getDonGia();

                if (giaCuTrongHD.compareTo(giaMoi) != 0) {
                    String strGiaCu = String.format("%,.0f", giaCuTrongHD);
                    String strGiaMoi = String.format("%,.0f", giaMoi);

                    warningMessage = "Sản phẩm " + ctsp.getSanPham().getTen() + " đã đổi giá từ " + strGiaCu + " thành " + strGiaMoi;
                }
            }

            hdct = new HoaDonChiTiet();
            hdct.setHoaDon(hd);
            hdct.setChiTietSanPham(ctsp);
            hdct.setSoLuong(soLuong);
            hdct.setDonGia(giaMoi);
            hdct.setThanhTien(giaMoi.multiply(BigDecimal.valueOf(soLuong)));
            hdct.setTrangThai(trangThaiHoaDonChiTiet.DANG_BAN.getValue());
        }

        hoaDonChiTietRepository.saveAndFlush(hdct);

        ctsp.setSoLuongTon(ctsp.getSoLuongTon() - soLuong);
        chiTietSanPhamRepository.save(ctsp);

        capNhatLaiTongTienVaKhuyenMai(hd);

        List<HoaDonChiTiet> allRelatedHdct = hoaDonChiTietRepository
                .findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham);

        return new ThemSanPhamResponse(hdct, allRelatedHdct, warningMessage);
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

        capNhatLaiTongTienVaKhuyenMai(hd);

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
                pggCaNhanOpt = phieuGiamGiaCaNhanRepository.findByKhachHangAndPhieuGiamGia(hd.getKhachHang(), pgg);
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
                capNhatLaiTongTienVaKhuyenMai(hd);
                throw new LoiPhieuGiamGiaException(lyDo + " Vui lòng kiểm tra lại đơn hàng!");
            }
        }

        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

        hd.setLoaiHoaDon(request.getLoaiHoaDon());
        BigDecimal phiShip = BigDecimal.ZERO;

        if ("Online".equalsIgnoreCase(request.getLoaiHoaDon())) {
            phiShip = request.getPhiVanChuyen() != null ? request.getPhiVanChuyen() : BigDecimal.ZERO;

            hd.setPhiVanChuyen(phiShip);
            hd.setTenKhachHang(request.getTenKhachHang());
            hd.setSdt(request.getSdt());
            hd.setDiaChiGiaoHang(request.getDiaChiGiaoHang());
            hd.setTrangThai(trangThaiHoaDon.CHO_XAC_NHAN.getValue());

            KhachHang kh = hd.getKhachHang();
            if (kh != null && request.getTinhThanh() != null) {
                List<DiaChi> listDiaChi = diaChiRepository.findByKhachHangId(kh.getId());
                if (listDiaChi.isEmpty()) {
                    DiaChi diaChiMoi = new DiaChi();
                    diaChiMoi.setKhachHang(kh);
                    diaChiMoi.setThanhPho(request.getTinhThanh());
                    diaChiMoi.setHuyen(request.getQuanHuyen());
                    diaChiMoi.setXa(request.getPhuongXa());
                    diaChiMoi.setDiaChiCuThe(request.getDiaChiCuThe());
                    diaChiMoi.setMacDinh(true);
                    diaChiMoi.setMa(generateNewMaDiaChi());

                    diaChiRepository.save(diaChiMoi);
                }
            }
        } else {
            hd.setPhiVanChuyen(BigDecimal.ZERO);
            hd.setTrangThai(trangThaiHoaDon.HOAN_THANH.getValue());
        }

        BigDecimal tongTienHang = tinhTongTien(idHoaDon);

        BigDecimal soTienGiam = tinhSoTienDuocGiam(tongTienHang, hd.getPhieuGiamGia());

        BigDecimal tongTienSauTruVoucher = tongTienHang.subtract(soTienGiam);
        if (tongTienSauTruVoucher.compareTo(BigDecimal.ZERO) < 0) tongTienSauTruVoucher = BigDecimal.ZERO;

        BigDecimal tongThanhToanCuoiCung = tongTienSauTruVoucher.add(phiShip);

        hd.setTongTien(tongTienHang);
        hd.setTongTienSauGiam(tongThanhToanCuoiCung);
        hd.setNgaySua(LocalDate.now());
        hd.setNguoiSua("Nhân viên quầy");

        HinhThucThanhToan httt = new HinhThucThanhToan();
        httt.setHoaDon(hd);
        httt.setPhuongThucThanhToan(pttt);
        httt.setSoTien(tongThanhToanCuoiCung);
        httt.setGhiChu(request.getLoaiHoaDon());
        hinhThucThanhToanRepository.save(httt);

        List<HoaDonChiTiet> list = hoaDonChiTietRepository.findAllByHoaDonId(idHoaDon);
        for (HoaDonChiTiet hdct : list) {
            hdct.setTrangThai(trangThaiHoaDonChiTiet.DA_THANH_TOAN.getValue());
        }
        hoaDonChiTietRepository.saveAll(list);

        return hoaDonRepository.save(hd);
    }

    public HoaDonVoucherCheckResponse kiemTraVoucher(UUID hoaDonId) {
        HoaDon hd = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        BigDecimal tongTienHang = tinhTongTien(hoaDonId);

        PhieuGiamGia pggHienTai = hd.getPhieuGiamGia();
        BigDecimal giamHienTai = tinhSoTienDuocGiam(tongTienHang, pggHienTai);

        PhieuGiamGia pggTotNhat = timPhieuGiamGiaTotNhat(
                hoaDonId,
                hd.getKhachHang() != null ? hd.getKhachHang().getId() : null
        );

        BigDecimal giamTotNhat = tinhSoTienDuocGiam(tongTienHang, pggTotNhat);

        HoaDonVoucherCheckResponse res = new HoaDonVoucherCheckResponse();
        res.setPhieuGiamGiaDangApDung(mapToVoucherInfo(pggHienTai));
        res.setPhieuGiamGiaTotNhat(mapToVoucherInfo(pggTotNhat));
        res.setSoTienGiamHienTai(giamHienTai);
        res.setSoTienGiamTotNhat(giamTotNhat);

        res.setCoMaTotHon(
                pggTotNhat != null &&
                        (pggHienTai == null ||
                                giamTotNhat.compareTo(giamHienTai) > 0)
        );

        return res;
    }

    private VoucherInfo mapToVoucherInfo(PhieuGiamGia pgg) {
        if (pgg == null) return null;

        VoucherInfo info = new VoucherInfo();
        info.setId(pgg.getId());
        info.setMa(pgg.getMa());
        info.setTen(pgg.getTen());

        info.setHinhThucGiamGia(pgg.getHinhThucGiamGia());
        info.setGiaTriGiam(pgg.getGiaTriGiam());
        info.setGiaTriGiamToiDa(pgg.getGiaTriGiamToiDa());
        info.setGiaTriGiamToiThieu(pgg.getGiaTriGiamToiThieu());

        info.setNgayBatDau(pgg.getNgayBatDau());
        info.setNgayKetThuc(pgg.getNgayKetThuc());

        return info;
    }


    public String generateNewMaDiaChi() {
        String maxMa = diaChiRepository.findMaxMa();

        if (maxMa == null) return "DC01";

        try {
            int num = Integer.parseInt(maxMa.substring(2)) + 1;
            return String.format("DC%02d", num);
        } catch (NumberFormatException e) {
            return "DC" + (System.currentTimeMillis() % 10000);
        }
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

        if (hd.getTrangThai() == trangThaiHoaDon.HOAN_THANH.getValue() ||
                hd.getTrangThai() == trangThaiHoaDon.DA_HUY.getValue()) {
            return;
        }

        List<HoaDonChiTiet> listHdct = hoaDonChiTietRepository.findAllByHoaDonId(idHoaDon);
        for (HoaDonChiTiet hdct : listHdct) {
            if (hdct.getTrangThai() == trangThaiHoaDonChiTiet.DANG_BAN.getValue()) {
                ChiTietSanPham ctsp = hdct.getChiTietSanPham();
                ctsp.setSoLuongTon(ctsp.getSoLuongTon() + hdct.getSoLuong());
                chiTietSanPhamRepository.save(ctsp);
            }
        }

        hd.setTrangThai(trangThaiHoaDon.DA_HUY.getValue());
        hd.setNgaySua(LocalDate.now());
        hd.setNguoiSua("Admin/Staff");
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

        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        capNhatLaiTongTienVaKhuyenMai(hd);
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
        String newMa = "KH" + String.format("%02d", newNumber);

        return newMa;
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

    public void capNhatLaiTongTienVaKhuyenMai(HoaDon hd) {
        BigDecimal tongTienHang = hoaDonChiTietRepository.tongTienHoaDon(hd.getId());
        if (tongTienHang == null) tongTienHang = BigDecimal.ZERO;

        BigDecimal soTienGiam = BigDecimal.ZERO;
        if (hd.getPhieuGiamGia() != null) {
            soTienGiam = tinhSoTienDuocGiam(tongTienHang, hd.getPhieuGiamGia());
        }

        BigDecimal tongTienSauTruVoucher = tongTienHang.subtract(soTienGiam);
        if (tongTienSauTruVoucher.compareTo(BigDecimal.ZERO) < 0) {
            tongTienSauTruVoucher = BigDecimal.ZERO;
        }

        BigDecimal phiVanChuyen = hd.getPhiVanChuyen() != null
                ? hd.getPhiVanChuyen()
                : BigDecimal.ZERO;

        BigDecimal tongTienSauGiam = tongTienSauTruVoucher.add(phiVanChuyen);

        hd.setTongTien(tongTienHang);
        hd.setTongTienSauGiam(tongTienSauGiam);

        hoaDonRepository.save(hd);
    }

    private BigDecimal tinhSoTienDuocGiam(BigDecimal tongTienHang, PhieuGiamGia phieu) {
        if (phieu == null) return BigDecimal.ZERO;

        if (phieu.getGiaTriGiamToiThieu() != null
                && tongTienHang.compareTo(phieu.getGiaTriGiamToiThieu()) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal soTienGiam = BigDecimal.ZERO;

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
            return tongTienHang;
        }

        return soTienGiam;
    }

    @Override
    @Transactional
    public HoaDon capNhatThongTinHoaDon(UUID idHoaDon, ThanhToanRequest request) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (request.getTenKhachHang() != null) hd.setTenKhachHang(request.getTenKhachHang());
        if (request.getSdt() != null) hd.setSdt(request.getSdt());
        if (request.getDiaChiGiaoHang() != null) hd.setDiaChiGiaoHang(request.getDiaChiGiaoHang());
        if (request.getLoaiHoaDon() != null) hd.setLoaiHoaDon(request.getLoaiHoaDon());

        boolean coThayDoiLienQuanTien = false;

        if (request.getPhiVanChuyen() != null) {
            hd.setPhiVanChuyen(request.getPhiVanChuyen());
            coThayDoiLienQuanTien = true;
        }

        hd = hoaDonRepository.saveAndFlush(hd);

        if (coThayDoiLienQuanTien) {
            capNhatLaiTongTienVaKhuyenMai(hd);
        }

        return hd;
    }
}