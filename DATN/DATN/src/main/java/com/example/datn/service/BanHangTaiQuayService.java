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
        // (Sử dụng hàm repo mới số 1 của bạn)
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
            // Tìm xem sản phẩm này đã từng tồn tại trong giỏ với giá khác chưa?
            // (Sử dụng hàm repo mới số 2 của bạn)
            List<HoaDonChiTiet> listCu = hoaDonChiTietRepository
                    .findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham);

            if (!listCu.isEmpty()) {
                // Lấy ra giá của sản phẩm cũ đầu tiên tìm thấy
                BigDecimal giaCu = listCu.get(0).getDonGia();
                // Format tiền cho đẹp (bỏ số 0 sau dấu phẩy)
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

        // 4. Lưu và Đẩy ngay xuống DB (Quan trọng để hàm tính tiền query được)
        hoaDonChiTietRepository.saveAndFlush(hdct);

        // 5. Cập nhật tồn kho
        ctsp.setSoLuongTon(ctsp.getSoLuongTon() - soLuong);
        chiTietSanPhamRepository.save(ctsp);

        // 6. Tính lại tiền (Dùng ID để gọi hàm đã tách Transaction/Update Query)
        capNhatLaiTongTienVaKhuyenMai(idHoaDon);

        // 7. Trả về DTO chứa cả Data và Message
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

//        capNhatLaiTongTienVaKhuyenMai(idHoaDon);

        return hoaDonRepository.save(hd);
    }

    @Override
    public HoaDon apDungPhieuGiamGia(UUID idHoaDon, UUID idPhieuGiamGia) {
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idPhieuGiamGia)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));
//        BigDecimal tongTien = tinhTongTien(idHoaDon);
//        BigDecimal giam = pgg.getGiaTriGiam() != null ? pgg.getGiaTriGiam() : BigDecimal.ZERO;
//        hd.setTongTien(tongTien);
//        hd.setTongTienSauGiam(tongTien.subtract(giam));
//        hd.setPhieuGiamGia(pgg);
//        return hoaDonRepository.save(hd);

        // 🔥 BƯỚC 1: CẬP NHẬT PHIẾU GIẢM GIÁ VÀO HÓA ĐƠN
        hd.setPhieuGiamGia(pgg);
        // Lưu tạm thời để hàm tính toán có thể đọc được PGG mới
        hoaDonRepository.save(hd);

        // 🔥 BƯỚC 2: GỌI HÀM TÍNH TOÁN LẠI TỔNG TIỀN (Sẽ tính toán lại tổng tiền và tongTienSauGiam)
        capNhatLaiTongTienVaKhuyenMai(idHoaDon);

        return hd;
    }

    @Override
    @Transactional(noRollbackFor = {LoiPhieuGiamGiaException.class})
    public HoaDon thanhToan(UUID idHoaDon, ThanhToanRequest request) { // 🔥 Nhận DTO

        // 1. Tìm hóa đơn
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        PhieuGiamGia pgg = hd.getPhieuGiamGia();

        if (pgg != null) {
            boolean isLoi = false;
            String lyDo = "";
            Date now = new Date();

            // A. Kiểm tra xem đây có phải là Phiếu Cá Nhân của khách hàng này không?
            Optional<PhieuGiamGiaCaNhan> pggCaNhanOpt = Optional.empty();

            if (hd.getKhachHang() != null) {
                pggCaNhanOpt = phieuGiamGiaCaNhanRepository.findByKhachHangAndPhieuGiamGia(
                        hd.getKhachHang(),
                        pgg
                );
            }

            if (pggCaNhanOpt.isPresent()) {
                // === TRƯỜNG HỢP 1: ĐÂY LÀ PHIẾU CÁ NHÂN ===
                PhieuGiamGiaCaNhan pggCaNhan = pggCaNhanOpt.get();

                // 1. Check Trạng thái cá nhân (0: Ngừng, 1: Hoạt động) -> Lưu ý kiểu BIT trong DB là Boolean
                if (Boolean.FALSE.equals(pggCaNhan.getTrangThai())) {
                    isLoi = true;
                    lyDo = "Phiếu giảm giá cá nhân của bạn đã bị vô hiệu hóa.";
                }
                // 2. Check Ngày hết hạn cá nhân
                else if (pggCaNhan.getNgayHetHan() != null && now.after(pggCaNhan.getNgayHetHan())) {
                    isLoi = true;
                    lyDo = "Phiếu giảm giá cá nhân của bạn đã hết hạn sử dụng.";
                }
                // (Phiếu cá nhân thường không check ngày bắt đầu vì đã được nhận rồi, nhưng nếu cần có thể check thêm)

            } else {
                // === TRƯỜNG HỢP 2: ĐÂY LÀ PHIẾU CÔNG KHAI (Hoặc phiếu chung) ===

                // 1. Check Trạng thái chung
                if (Boolean.FALSE.equals(pgg.getTrangThai())) {
                    isLoi = true;
                    lyDo = "Chương trình khuyến mãi này đã kết thúc.";
                }
                // 2. Check Ngày kết thúc chung
                else if (pgg.getNgayKetThuc() != null && now.after(pgg.getNgayKetThuc())) {
                    isLoi = true;
                    lyDo = "Phiếu giảm giá đã hết hạn chương trình.";
                }
                // 3. Check Ngày bắt đầu chung
                else if (pgg.getNgayBatDau() != null && now.before(pgg.getNgayBatDau())) {
                    isLoi = true;
                    lyDo = "Chương trình khuyến mãi chưa bắt đầu.";
                }
            }

            // === XỬ LÝ NẾU CÓ LỖI (CHUNG CHO CẢ 2 TRƯỜNG HỢP) ===
            if (isLoi) {
                // A. Gỡ phiếu
                hd.setPhieuGiamGia(null);

                hoaDonRepository.saveAndFlush(hd);

                // B. Tính lại tiền
                // (Gọi hàm update an toàn đã fix ở các bước trước)
                capNhatLaiTongTienVaKhuyenMai(idHoaDon);

                // C. Báo lỗi chặn thanh toán
                throw new LoiPhieuGiamGiaException(lyDo + " Vui lòng kiểm tra lại đơn hàng!");
            }
        }

        // 2. Tìm phương thức thanh toán (Lấy ID từ request)
        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

        // 3. Tính tiền hàng
        BigDecimal tongTienHang = tinhTongTien(idHoaDon);
        BigDecimal phiShip = BigDecimal.ZERO;

        // === 🔥 LOGIC MỚI: CẬP NHẬT THÔNG TIN GIAO HÀNG ===
        hd.setLoaiHoaDon(request.getLoaiHoaDon());

        if ("Online".equalsIgnoreCase(request.getLoaiHoaDon())) {
            // Lưu thông tin người nhận
            hd.setTenKhachHang(request.getTenKhachHang());
            hd.setSdt(request.getSdt());
            hd.setDiaChiGiaoHang(request.getDiaChiGiaoHang());

            // Xử lý phí ship (Nếu null thì coi như 0)
            phiShip = request.getDiaChiGiaoHang() != null ? request.getPhiVanChuyen() : BigDecimal.ZERO;
            hd.setPhiVanChuyen(phiShip);

            // 💡 Logic trạng thái:
            // Nếu giao hàng, thường trạng thái không phải là HOAN_THANH ngay
            // mà là CHO_GIAO_HANG (tùy nghiệp vụ của bạn).
            // Tạm thời mình để HOAN_THANH theo code cũ của bạn, nhưng bạn nên cân nhắc.
            hd.setTrangThai(trangThaiHoaDon.HOAN_THANH.getValue());

        } else {
            // Nếu bán tại quầy, set phí ship về 0 và xóa thông tin người nhận (để sạch data)
            hd.setPhiVanChuyen(BigDecimal.ZERO);
            hd.setTenKhachHang(null); // Hoặc giữ nguyên tên khách mua
            hd.setSdt(null);
            hd.setTrangThai(trangThaiHoaDon.HOAN_THANH.getValue());
        }

        // 4. Tính lại tổng tiền thanh toán = Tiền Hàng + Ship - Giảm Giá (nếu có)
        // Giả sử tongTienSauGiam hiện tại đang lưu (Tiền hàng - Voucher)
        // Thì giờ phải cộng thêm Ship vào

        // Cách an toàn: Tính lại từ đầu
        // BigDecimal tongThanhToan = tongTienHang.add(phiShip).subtract(hd.getGiamGia() != null ? hd.getGiamGia() : BigDecimal.ZERO);

        // Hoặc đơn giản theo code của bạn (nếu chưa có giảm giá phức tạp):
        BigDecimal tongThanhToan = tongTienHang.add(phiShip);

        hd.setTongTien(tongTienHang); // Tổng tiền hàng chưa ship
        hd.setTongTienSauGiam(tongThanhToan); // Tổng phải trả cuối cùng

        // 5. Tạo lịch sử thanh toán
        HinhThucThanhToan httt = new HinhThucThanhToan();
        httt.setHoaDon(hd);
        httt.setPhuongThucThanhToan(pttt);
        httt.setSoTien(tongThanhToan); // 🔥 Lưu số tiền thực trả (gồm ship)
        httt.setGhiChu(request.getLoaiHoaDon()); // Ghi chú là thanh toán Online hay Tại quầy
        hinhThucThanhToanRepository.save(httt);

        // 6. Cập nhật thông tin chung
        hd.setNgaySua(LocalDate.now());
        hd.setNguoiSua("nhân viên quầy");

        // 7. Cập nhật trạng thái chi tiết sản phẩm
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

        // 1. Tìm tất cả các dòng của sản phẩm này trong hóa đơn (List thay vì Optional)
        // (Vì có thể có dòng giá 400k và dòng giá 500k của cùng 1 SP)
        List<HoaDonChiTiet> listHdct = hoaDonChiTietRepository
                .findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham);

        if (listHdct.isEmpty()) {
            System.out.println("⚠️ Không tìm thấy sản phẩm trong hóa đơn!");
            return;
        }

        // 2. Duyệt qua từng dòng để xử lý (Hoàn tồn + Xóa mềm)
        for (HoaDonChiTiet hdct : listHdct) {
            // Chỉ xóa những dòng chưa bị xóa (đề phòng)
            if (hdct.getTrangThai() != trangThaiHoaDonChiTiet.DA_XOA.getValue()) {

                System.out.println("✅ Tìm thấy chi tiết hóa đơn ID: " + hdct.getId() + ", SL = " + hdct.getSoLuong() + ", Giá = " + hdct.getDonGia());

                // A. Hoàn lại tồn kho
                ChiTietSanPham ctsp = hdct.getChiTietSanPham();
                ctsp.setSoLuongTon(ctsp.getSoLuongTon() + hdct.getSoLuong());
                chiTietSanPhamRepository.save(ctsp);

                System.out.println("🔄 Hoàn lại tồn cho sản phẩm: " + ctsp.getId() + ", tồn mới = " + ctsp.getSoLuongTon());

                // B. Cập nhật trạng thái XÓA
                hdct.setTrangThai(trangThaiHoaDonChiTiet.DA_XOA.getValue());

                // 🔥 Quan trọng: Dùng saveAndFlush để DB cập nhật ngay
                hoaDonChiTietRepository.saveAndFlush(hdct);
            }
        }

        // 3. Tính lại tiền (Dùng ID để tránh lỗi cache)
        capNhatLaiTongTienVaKhuyenMai(idHoaDon);
    }

    public List<KhachHang> timKhachHangByHotenOrSdt(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("🔍 Từ khóa tìm kiếm rỗng. Trả về danh sách rỗng.");
            // Trả về danh sách rỗng thay vì Optional.empty()
            return Collections.emptyList();
        }

        String searchKeyword = keyword.trim();
        System.out.println("🔍 Tìm kiếm DANH SÁCH Khách hàng theo Tên hoặc SĐT: " + searchKeyword);

        // 💡 THAY ĐỔI: Gọi hàm Repository mới trả về List
        // (Giả sử bạn đã định nghĩa hàm này là timDanhSachKhachHang)
        List<KhachHang> khachHangList = khachHangRepository.timDanhSachKhachHang(searchKeyword);

        if (!khachHangList.isEmpty()) {
            System.out.println("✅ Tìm thấy " + khachHangList.size() + " Khách hàng.");
        } else {
            System.out.println("⚠️ Không tìm thấy Khách hàng với từ khóa: " + searchKeyword);
        }

        // 💡 THAY ĐỔI: Trả về danh sách
        return khachHangList;
    }

    @Transactional
    public KhachHang themKhachHangMoi(KhachHang khachHangMoi) {
        String sdt = khachHangMoi.getSdt();
        if (sdt != null) {
            sdt = sdt.trim(); // Loại bỏ khoảng trắng
        }

        khachHangMoi.setSdt(sdt);

        if (khachHangMoi.getHoTen() == null || khachHangMoi.getHoTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }

        if (sdt == null || sdt.isEmpty()) {
            throw new IllegalArgumentException("SĐT khách hàng không được để trống.");
        }

        // 1. Kiểm tra SĐT đã tồn tại chưa
        if (khachHangRepository.findBySdt(sdt).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        if (khachHangMoi.getNgayTao() == null) {
            khachHangMoi.setNgayTao(new Date());
        }

//         Thiết lập các giá trị mặc định (tùy thuộc vào Entity KhachHang của bạn)
        if (khachHangMoi.getMa() == null || khachHangMoi.getMa().trim().isEmpty()) {
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

    public PhieuGiamGia timPhieuGiamGiaTotNhat(UUID idHoaDon, UUID idKhachHang) {
        // 1. Lấy tổng tiền
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).orElseThrow();

        // Tính lại tổng tiền cho chắc ăn (giống controller)
        BigDecimal tongTienHang = BigDecimal.ZERO;
        if (hoaDon.getHoaDonChiTiets() != null) {
            for (HoaDonChiTiet ct : hoaDon.getHoaDonChiTiets()) {
                if (ct.getThanhTien() != null) tongTienHang = tongTienHang.add(ct.getThanhTien());
            }
        }
        System.out.println("🔥 [DEBUG] Tổng tiền đơn hàng: " + tongTienHang);

        // 2. Lấy danh sách phiếu từ DB
        List<PhieuGiamGia> listCoupons = phieuGiamGiaRepository.findValidCouponsForCustomer(idKhachHang);
        System.out.println("🔥 [DEBUG] Tìm thấy " + listCoupons.size() + " phiếu trong DB (thỏa mãn ngày & trạng thái).");

        PhieuGiamGia bestCoupon = null;
        BigDecimal maxDiscountAmount = BigDecimal.ZERO;

        for (PhieuGiamGia phieu : listCoupons) {
            System.out.println("  👉 Đang check phiếu: " + phieu.getTen() + " | Mã: " + phieu.getMa());

            // --- CHECK 1: Điều kiện giá trị đơn hàng tối thiểu ---
            BigDecimal dieuKienToiThieu = phieu.getGiaTriGiamToiThieu();
            if (dieuKienToiThieu != null && tongTienHang.compareTo(dieuKienToiThieu) < 0) {
                System.out.println("     ❌ BỊ LOẠI: Tổng tiền (" + tongTienHang + ") nhỏ hơn điều kiện tối thiểu (" + dieuKienToiThieu + ")");
                continue;
            }

            // --- TÍNH TOÁN ---
            BigDecimal currentDiscountAmount = BigDecimal.ZERO;
            if (Boolean.TRUE.equals(phieu.getHinhThucGiamGia())) {
                // Giảm %
                BigDecimal phanTram = phieu.getGiaTriGiam();
                currentDiscountAmount = tongTienHang.multiply(phanTram).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

                if (phieu.getGiaTriGiamToiDa() != null && currentDiscountAmount.compareTo(phieu.getGiaTriGiamToiDa()) > 0) {
                    currentDiscountAmount = phieu.getGiaTriGiamToiDa();
                    System.out.println("     ℹ️ Áp dụng giảm tối đa: " + currentDiscountAmount);
                }
            } else {
                // Giảm tiền
                currentDiscountAmount = phieu.getGiaTriGiam();
            }

            System.out.println("     ✅ HỢP LỆ. Mức giảm: " + currentDiscountAmount);

            // So sánh tìm Best
            if (currentDiscountAmount.compareTo(maxDiscountAmount) > 0) {
                maxDiscountAmount = currentDiscountAmount;
                bestCoupon = phieu;
                System.out.println("     ⭐️ Đây đang là phiếu ngon nhất!");
            }
        }

        return bestCoupon;
    }

    // TRONG BanHangTaiQuayService.java

    // Hàm này dùng để tính toán lại mọi thứ mỗi khi giỏ hàng thay đổi
    // TRONG BanHangTaiQuayService.java

    // 👇 Sửa tham số đầu vào thành UUID idHoaDon
    private void capNhatLaiTongTienVaKhuyenMai(UUID idHoaDon) {
        // 1. Lấy Hóa Đơn
        HoaDon hd = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        // Lấy thông tin phiếu giảm giá ra trước (để tránh lỗi Lazy khi detach)
        PhieuGiamGia phieu = hd.getPhieuGiamGia();

        // 🔥 BƯỚC QUAN TRỌNG NHẤT: DETACH (TÁCH) HÓA ĐƠN KHỎI CONTEXT
        // Để ngăn Hibernate tự động xóa sản phẩm mới do cơ chế đồng bộ danh sách
        entityManager.detach(hd);

        // 2. Tính tổng tiền từ DB (Query Native)
        BigDecimal tongTienHang = hoaDonChiTietRepository.tongTienHoaDon(idHoaDon);

        // 3. Tính toán Khuyến mãi (Logic cũ)
        BigDecimal soTienGiam = BigDecimal.ZERO;

        if (phieu != null) {
            // Check điều kiện tối thiểu
            if (phieu.getGiaTriGiamToiThieu() != null
                    && tongTienHang.compareTo(phieu.getGiaTriGiamToiThieu()) < 0) {
                phieu = null; // Gỡ phiếu
            } else {
                // Tính tiền giảm
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

        // 4. Tính tiền sau giảm
        BigDecimal tienSauGiam = tongTienHang.subtract(soTienGiam).max(BigDecimal.ZERO);

        // 5. UPDATE TRỰC TIẾP VÀO DB
        // Lúc này hd đã bị detach nên Hibernate sẽ không can thiệp vào list sản phẩm nữa
        hoaDonRepository.updateTienVaKhuyenMai(idHoaDon, tongTienHang, tienSauGiam, phieu);
    }
}
