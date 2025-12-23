// src/main/java/com/example/datn/service/HoaDonService.java
package com.example.datn.service;

import com.example.datn.common.HoaDonStatus;
import com.example.datn.entity.*;
import com.example.datn.model.Response.HoaDonChiTietResponse;
import com.example.datn.model.Response.HoaDonDetailResponse;
import com.example.datn.model.Response.HoaDonResponse;
import com.example.datn.model.request.CancelOrderRequest;
import com.example.datn.model.request.HoaDonFilterRequest;
import com.example.datn.model.request.TaoHoaDonRequest;
import com.example.datn.model.request.UpdateHoaDonRequest;
import com.example.datn.repository.*;
import com.example.datn.specification.HoaDonSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final MailService mailService;
    private final NhanVienRepository nhanVienRepository;

    private static final Map<Integer, String> TRANG_THAI_MAP = Map.of(
            HoaDonStatus.HOA_DON_CHO, "Chờ thanh toán",
            HoaDonStatus.CHO_XAC_NHAN, "Chờ xác nhận",
            HoaDonStatus.DA_XAC_NHAN, "Đã xác nhận",
            HoaDonStatus.DANG_CHUAN_BI, "Đang chuẩn bị",
            HoaDonStatus.DANG_GIAO, "Đang giao",
            HoaDonStatus.GIAO_HANG_THAT_BAI, "Giao hàng thất bại",
            HoaDonStatus.HOAN_THANH, "Hoàn thành",
            HoaDonStatus.DA_HUY, "Đã hủy"
    );

    /* ================== ADMIN: SEARCH ================== */
    public Page<HoaDonResponse> searchHoaDon(
            HoaDonFilterRequest filter,
            int page,
            int size
    ) {
        Specification<HoaDon> spec = HoaDonSpecification.filter(filter);

        Sort sort = Sort.by("ngayTao").ascending(); // mới ở cuối

        Pageable pageable = PageRequest.of(page, size, sort);

        return hoaDonRepository
                .findAll(spec, pageable)
                .map(HoaDonResponse::new);
    }

    /* ================== ADMIN: DETAIL BY ID (+ lịch sử) ================== */
    @Transactional(readOnly = true)
    public HoaDonDetailResponse getHoaDonDetail(UUID id) {

        // ✅ IMPORTANT: fetch join phiếu giảm giá để JSON/DTO không bị null
        HoaDon hoaDon = hoaDonRepository.findByIdFetchPhieu(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        if (hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getDanhSachDiaChi() != null) {
            hoaDon.getKhachHang().getDanhSachDiaChi().size();
        }

        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());
        List<HoaDonChiTietResponse> chiTietResponses = chiTietList.stream()
                .map(ct -> {
                    HoaDonChiTietResponse r = new HoaDonChiTietResponse(ct);
                    r.setUrlAnh1(pickImg(ct.getChiTietSanPham()));
                    return r;
                })
                .collect(Collectors.toList());

        List<LichSuHoaDon> lichSuList = lichSuHoaDonRepository.findAllByHoaDonId(id);

        return buildDetailResponse(hoaDon, chiTietResponses, lichSuList);
    }

    /* ================== PUBLIC: DETAIL BY CODE ================== */
    @Transactional(readOnly = true)
    public HoaDonDetailResponse getHoaDonDetailByCode(String code) {
        if (code == null || code.trim().isEmpty()) return null;

        // ✅ IMPORTANT: fetch join phiếu giảm giá để FE đọc phieuGiamGiaResponse/ten/ma
        HoaDon hoaDon = hoaDonRepository.findByMaFetchPhieu(code.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());
        List<HoaDonChiTietResponse> chiTietResponses = chiTietList.stream()
                .map(ct -> {
                    HoaDonChiTietResponse r = new HoaDonChiTietResponse(ct);
                    r.setUrlAnh1(pickImg(ct.getChiTietSanPham()));
                    return r;
                })
                .collect(Collectors.toList());

        List<LichSuHoaDon> lichSuList = lichSuHoaDonRepository.findAllByHoaDonId(hoaDon.getId());

        return buildDetailResponse(hoaDon, chiTietResponses, lichSuList);
    }

    /* ================== CUSTOMER: LIST MY ORDERS (BY EMAIL) ================== */
    @Transactional(readOnly = true)
    public List<HoaDonResponse> getMyOrdersByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }
        return hoaDonRepository
                .findAllByEmailKhachHangIgnoreCaseOrderByNgayTaoDesc(email.trim())
                .stream()
                .map(HoaDonResponse::new)
                .collect(Collectors.toList());
    }

    /* ================== ADMIN: UPDATE (+ lịch sử, + trừ kho khi cần) ================== */
    @Transactional
    public HoaDonResponse updateHoaDon(UUID id, UpdateHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (request == null || request.getTrangThai() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu trạng thái cập nhật");
        }

        Integer trangThaiCu = hoaDon.getTrangThai();
        String sdtCu = hoaDon.getSdt();
        String diaChiCu = hoaDon.getDiaChiGiaoHang();
        String tenKhachHangCu = hoaDon.getTenKhachHang();

        Integer trangThaiMoi = request.getTrangThai();

        String sdtMoi = (request.getSdt() == null || request.getSdt().trim().isEmpty())
                ? sdtCu
                : normalizePhone(request.getSdt());

        String diaChiMoi = (request.getDiaChiGiaoHang() == null || request.getDiaChiGiaoHang().trim().isEmpty())
                ? diaChiCu
                : normalizeText(request.getDiaChiGiaoHang());

        String tenKhachHangMoi = (request.getTenKhachHang() == null || request.getTenKhachHang().trim().isEmpty())
                ? tenKhachHangCu
                : normalizeText(request.getTenKhachHang());

        String loaiHD = hoaDon.getLoaiHoaDon();
        String loai = loaiHD == null ? "" : loaiHD.trim();

        boolean isGiaoHang =
                loai.equalsIgnoreCase("online") ||
                        loai.equalsIgnoreCase("giao hàng");

        if (isGiaoHang && (diaChiMoi == null || diaChiMoi.isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Đơn hàng giao hàng bắt buộc phải có địa chỉ giao hàng!"
            );
        }

        boolean wasConfirmedOrLater =
                trangThaiCu != null && trangThaiCu >= HoaDonStatus.DA_XAC_NHAN;
        boolean nowConfirmedOrLater =
                trangThaiMoi != null && trangThaiMoi >= HoaDonStatus.DA_XAC_NHAN;
        boolean needTruKho = !wasConfirmedOrLater && nowConfirmedOrLater;

        hoaDon.setTrangThai(trangThaiMoi);
        hoaDon.setSdt(sdtMoi);
        hoaDon.setDiaChiGiaoHang(diaChiMoi);
        hoaDon.setTenKhachHang(tenKhachHangMoi);

        HoaDon saved = hoaDonRepository.save(hoaDon);

        if (needTruKho) {
            truKhoChoHoaDon(saved);
        }

        String actor = "System/Anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String principal = auth.getName();
            if (principal.length() > 3 && principal.contains(" ")) {
                actor = principal;
            }

            try {
                NhanVien nhanVien = nhanVienRepository.findByTaiKhoan(principal)
                        .orElse(null);

                if (nhanVien != null && nhanVien.getHoTen() != null && !nhanVien.getHoTen().trim().isEmpty()) {
                    actor = nhanVien.getHoTen();
                } else {
                    actor = principal;
                }
            } catch (Exception e) {
                actor = principal;
            }
        }
        if (!Objects.equals(trangThaiCu, trangThaiMoi)) {
            saveHistory(saved, actor,
                    "Đổi trạng thái: "
                            + TRANG_THAI_MAP.getOrDefault(trangThaiCu, "Cũ")
                            + " -> "
                            + TRANG_THAI_MAP.getOrDefault(trangThaiMoi, "Mới"));
        }
        if (!Objects.equals(sdtCu, sdtMoi)) {
            saveHistory(saved, actor, "Đổi SĐT: " + nullToDash(sdtCu) + " -> " + nullToDash(sdtMoi));
        }
        if (!Objects.equals(diaChiCu, diaChiMoi)) {
            saveHistory(saved, actor, "Đổi địa chỉ: " + nullToDash(diaChiCu) + " -> " + nullToDash(diaChiMoi));
        }
        if (!Objects.equals(tenKhachHangCu, tenKhachHangMoi)) {
            saveHistory(saved, actor, "Đổi Tên KH: " + nullToDash(tenKhachHangCu) + " -> " + nullToDash(tenKhachHangMoi));
        }

        return new HoaDonResponse(saved);
    }

    /* ================== PUBLIC: CREATE ORDER (Checkout) ================== */
    @Transactional
    public HoaDonDetailResponse createHoaDon(TaoHoaDonRequest req) {
        HoaDon hd = new HoaDon();

        String ma = (req.getMaDon() != null && !req.getMaDon().isBlank())
                ? req.getMaDon()
                : String.valueOf(System.currentTimeMillis());

        hd.setMa(ma);
        hd.setLoaiHoaDon("ONLINE");
        hd.setNgayTao(LocalDate.now());
// ✅ tùy bạn: muốn tạo xong là "Chờ xác nhận" thì đổi dòng dưới thành HoaDonStatus.CHO_XAC_NHAN
        hd.setTrangThai(HoaDonStatus.CHO_XAC_NHAN);

        hd.setTenKhachHang(normalizeText(req.getHoTen()));
        hd.setEmailKhachHang(normEmail(req.getEmail()));
        hd.setSdt(normalizePhone(req.getSdt()));
        hd.setDiaChiGiaoHang(normalizeText(req.getDiaChi()));
        hd.setGhiChu(normalizeText(req.getGhiChu()));

        // ====== TÍNH TIỀN + ÁP PHIẾU ======
        BigDecimal tamTinh = safe(req.getTamTinh());
        BigDecimal phiShip = safe(req.getPhiVanChuyen());

        BigDecimal tongTruocGiam = tamTinh.add(phiShip);
        hd.setTongTien(tongTruocGiam);

        BigDecimal tongSauGiam = tongTruocGiam;

        // ✅ LẤY MÃ PHIẾU từ request
        String maPhieu = (req.getMaGiamGia() == null) ? "" : req.getMaGiamGia().trim();

        if (!maPhieu.isEmpty()) {
            PhieuGiamGia phieu = phieuGiamGiaRepository.findByMaIgnoreCase(maPhieu)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã giảm giá không tồn tại"));

            if (phieu.getTrangThai() == null || !phieu.getTrangThai()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phiếu giảm giá đã ngừng hoạt động");
            }

            Date now = new Date();
            if (phieu.getNgayBatDau() != null && now.before(phieu.getNgayBatDau())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phiếu giảm giá chưa bắt đầu");
            }
            if (phieu.getNgayKetThuc() != null && now.after(phieu.getNgayKetThuc())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phiếu giảm giá đã hết hạn");
            }

            BigDecimal toiThieu = safe(phieu.getGiaTriGiamToiThieu());
            if (toiThieu.compareTo(BigDecimal.ZERO) > 0 && tongTruocGiam.compareTo(toiThieu) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đơn chưa đạt giá trị tối thiểu để áp mã");
            }

            BigDecimal giam;
            boolean isPercent = Boolean.TRUE.equals(phieu.getHinhThucGiamGia()); // true: %, false: tiền

            if (isPercent) {
                giam = tongTruocGiam.multiply(safe(phieu.getGiaTriGiam()))
                        .divide(BigDecimal.valueOf(100));
            } else {
                giam = safe(phieu.getGiaTriGiam());
            }

            BigDecimal toiDa = safe(phieu.getGiaTriGiamToiDa());
            if (toiDa.compareTo(BigDecimal.ZERO) > 0 && giam.compareTo(toiDa) > 0) {
                giam = toiDa;
            }

            if (giam.compareTo(BigDecimal.ZERO) < 0) giam = BigDecimal.ZERO;
            if (giam.compareTo(tongTruocGiam) > 0) giam = tongTruocGiam;

            tongSauGiam = tongTruocGiam.subtract(giam);

            // ✅ QUAN TRỌNG: set vào hóa đơn để DB có id_phieu_giam_gia
            hd.setPhieuGiamGia(phieu);

        } else {
            BigDecimal giamGiaTien = safe(req.getGiamGia());
            if (giamGiaTien.compareTo(BigDecimal.ZERO) > 0) {
                if (giamGiaTien.compareTo(tongTruocGiam) > 0) giamGiaTien = tongTruocGiam;
                tongSauGiam = tongTruocGiam.subtract(giamGiaTien);
            }
            hd.setPhieuGiamGia(null);
        }

        hd.setTongTienSauGiam(tongSauGiam);
        // ====== END TÍNH TIỀN ======

        HoaDon hoaDonSaved = hoaDonRepository.save(hd);

        List<HoaDonChiTiet> savedItems = new ArrayList<>();
        List<HoaDonChiTietResponse> chiTietResponses = new ArrayList<>();

        if (req.getItems() != null) {
            for (TaoHoaDonRequest.Item item : req.getItems()) {
                HoaDonChiTiet ct = new HoaDonChiTiet();
                ct.setHoaDon(hoaDonSaved);

                ChiTietSanPham ctsp = null;
                if (item.getChiTietSanPhamId() != null) {
                    ctsp = chiTietSanPhamRepository.findById(item.getChiTietSanPhamId()).orElse(null);
                }

                ct.setChiTietSanPham(ctsp);
                ct.setTenSanPham(item.getTenSanPham());
                ct.setSize(item.getSize());

                int soLuong = item.getSoLuong() == null ? 1 : item.getSoLuong();
                ct.setSoLuong(soLuong);

                BigDecimal donGia = item.getDonGia() != null
                        ? item.getDonGia()
                        : (ctsp != null && ctsp.getGiaBan() != null ? ctsp.getGiaBan() : BigDecimal.ZERO);

                ct.setDonGia(donGia);
                ct.setThanhTien(donGia.multiply(BigDecimal.valueOf(soLuong)));
                ct.setTrangThai(1);

                HoaDonChiTiet savedCt = hoaDonChiTietRepository.save(ct);
                savedItems.add(savedCt);

                HoaDonChiTietResponse r = new HoaDonChiTietResponse(savedCt);
                r.setUrlAnh1(pickImg(ctsp));
                chiTietResponses.add(r);
            }
        }

        saveHistory(hoaDonSaved, "SYSTEM", "Tạo đơn hàng ONLINE");

        try {
            mailService.sendOrderConfirmation(hoaDonSaved, savedItems);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ RETURN: nếu constructor DTO có map phieuGiamGiaResponse thì FE sẽ hiện
        return buildDetailResponse(hoaDonSaved, chiTietResponses, Collections.emptyList());
    }

    /* ================== PUBLIC: CANCEL ORDER BY CODE (NO LOGIN) ================== */
    @Transactional
    public void cancelPublicByCode(String code, CancelOrderRequest req) {
        if (code == null || code.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu mã đơn hàng");
        }

        HoaDon hd = hoaDonRepository.findByMa(code.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));
        int st = hd.getTrangThai() == null ? HoaDonStatus.HOA_DON_CHO : hd.getTrangThai();

        boolean allowCancel = (st == HoaDonStatus.HOA_DON_CHO || st == HoaDonStatus.CHO_XAC_NHAN);
        if (!allowCancel) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn hàng không thể hủy ở trạng thái hiện tại");
        }

        if (st == HoaDonStatus.DA_HUY) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn hàng đã hủy trước đó");
        }

        String reqEmail = normEmail(req != null ? req.getEmail() : null);
        String reqPhone = normPhone(req != null ? req.getPhone() : null);

        String hdEmail = normEmail(hd.getEmailKhachHang());
        String hdPhone = normPhone(hd.getSdt());

        boolean okEmail = !reqEmail.isEmpty() && reqEmail.equals(hdEmail);
        boolean okPhone = !reqPhone.isEmpty() && reqPhone.equals(hdPhone);

        if (!okEmail && !okPhone) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email/SĐT không khớp với đơn hàng");
        }

        hd.setTrangThai(HoaDonStatus.DA_HUY);
        hoaDonRepository.save(hd);

        saveHistory(hd, "CUSTOMER", "Khách hủy đơn (public)");
    }

    /* ================== PRIVATE HELPERS ================== */
    private void saveHistory(HoaDon hoaDon, String actor, String note) {
        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setHoaDon(hoaDon);
        ls.setThoiGian(LocalDateTime.now());
        ls.setNguoiThucHien(actor);
        ls.setGhiChu(note);
        lichSuHoaDonRepository.save(ls);
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "N/A" : s;
    }

    private String normalizeText(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // ✅ normalize SĐT: chỉ giữ số + xử lý 84 -> 0
    private String normalizePhone(String s) {
        if (s == null) return null;
        String t = s.trim().replaceAll("[^0-9]", "");
        if (t.startsWith("84")) t = "0" + t.substring(2);
        if (t.isEmpty()) return null;
        if (t.length() > 10) t = t.substring(0, 10);
        return t;
    }

    private void truKhoChoHoaDon(HoaDon hoaDon) {
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());
        if (chiTietList.isEmpty()) return;

        for (HoaDonChiTiet cthd : chiTietList) {
            ChiTietSanPham ctsp = cthd.getChiTietSanPham();
            if (ctsp == null) continue;

            int soLuongDat = cthd.getSoLuong() == null ? 0 : cthd.getSoLuong();
            int tonHienTai = ctsp.getSoLuongTon() == null ? 0 : ctsp.getSoLuongTon();

            if (soLuongDat <= 0) continue;
            if (tonHienTai < soLuongDat) {
                throw new RuntimeException("Không đủ số lượng hàng trong kho cho sản phẩm: "
                        + (cthd.getTenSanPham() != null ? cthd.getTenSanPham() : "Không rõ"));
            }

            ctsp.setSoLuongTon(tonHienTai - soLuongDat);
            chiTietSanPhamRepository.save(ctsp);
        }
    }

    /* ====== pick img ====== */
    private String pickImg(ChiTietSanPham ctsp) {
        if (ctsp == null) return null;

        String raw = firstNonBlank(
                readString(ctsp, "urlAnh1", "urlAnh", "anh1", "anh", "image", "hinhAnhUrl", "anhSanPham", "duongDanAnh"),
                readStringNested(ctsp, "hinhAnh", "urlAnh1"),
                readStringNested(ctsp, "hinhAnh", "urlAnh"),
                readStringNested(ctsp, "sanPham", "hinhAnh", "urlAnh1"),
                readStringNested(ctsp, "sanPham", "hinhAnh", "urlAnh"),
                readStringNested(ctsp, "sanPham", "urlAnh1")
        );
        return normalizeImg(raw);
    }

    private String firstNonBlank(String... arr) {
        if (arr == null) return null;
        for (String s : arr) {
            if (s != null) {
                String t = s.trim();
                if (!t.isEmpty()) return t;
            }
        }
        return null;
    }

    private String readString(Object obj, String... props) {
        if (obj == null || props == null) return null;
        for (String p : props) {
            Object v = readProperty(obj, p);
            if (v instanceof String) {
                String str = (String) v;
                String s = str.trim();
                if (!s.isEmpty()) return s;
            }
        }
        return null;
    }

    private String readStringNested(Object obj, String... path) {
        Object cur = obj;
        for (String p : path) {
            if (cur == null) return null;
            cur = readProperty(cur, p);
        }
        if (cur instanceof String) {
            String str = (String) cur;
            String s = str.trim();
            if (!s.isEmpty()) return s;
        }
        return null;
    }

    private Object readProperty(Object obj, String prop) {
        if (obj == null || prop == null) return null;

        Class<?> c = obj.getClass();
        String up = prop.substring(0, 1).toUpperCase() + prop.substring(1);

        try {
            Method m = c.getMethod("get" + up);
            return m.invoke(obj);
        } catch (Exception ignored) {
        }

        try {
            Method m = c.getMethod("is" + up);
            return m.invoke(obj);
        } catch (Exception ignored) {
        }

        try {
            Field f = c.getDeclaredField(prop);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception ignored) {
        }

        return null;
    }

    private String normalizeImg(String raw) {
        if (raw == null) return null;

        String s = raw.trim().replace("\\", "/");
        if (s.isEmpty()) return null;
        if (s.startsWith("http://") || s.startsWith("https://")) return s;

        if (s.matches("^[a-zA-Z]:/.*")) {
            s = s.substring(s.lastIndexOf("/") + 1);
        }

        if (!s.startsWith("/") && (s.startsWith("uploads/") || s.startsWith("upload/"))) {
            s = "/" + s;
        }

        if (!s.startsWith("/") && s.matches(".*\\.(png|jpg|jpeg|webp|gif|svg)$")) {
            s = "/uploads/" + s;
        }

        return s;
    }

    private String normEmail(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private String normPhone(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("[\\s.\\-()]", "");
    }

    private HoaDonDetailResponse buildDetailResponse(HoaDon hd,
                                                     List<HoaDonChiTietResponse> items,
                                                     List<LichSuHoaDon> history) {
        try {
            Constructor<HoaDonDetailResponse> c3 =
                    HoaDonDetailResponse.class.getConstructor(HoaDon.class, List.class, List.class);
            return c3.newInstance(hd, items, history);
        } catch (Exception ignore) {
            try {
                Constructor<HoaDonDetailResponse> c2 =
                        HoaDonDetailResponse.class.getConstructor(HoaDon.class, List.class);
                return c2.newInstance(hd, items);
            } catch (Exception e2) {
                throw new RuntimeException("Không khởi tạo được HoaDonDetailResponse (thiếu constructor phù hợp)", e2);
            }
        }
    }
}