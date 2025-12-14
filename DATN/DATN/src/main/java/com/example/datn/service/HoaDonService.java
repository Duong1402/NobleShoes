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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    private final MailService mailService;
    private final DiaChiRepository diaChiRepository;
    private final NhanVienRepository nhanVienRepository;

    private static final Map<Integer, String> TRANG_THAI_MAP = Map.of(
            HoaDonStatus.CHO_THANH_TOAN, "Chờ thanh toán",
            HoaDonStatus.CHO_XAC_NHAN, "Chờ xác nhận",
            HoaDonStatus.DA_XAC_NHAN, "Đã xác nhận",
            HoaDonStatus.DANG_GIAO, "Đang giao",
            HoaDonStatus.HOAN_THANH, "Hoàn thành",
            HoaDonStatus.DA_HUY, "Đã hủy"
    );

    /* ================== ADMIN: SEARCH ================== */
    public Page<HoaDonResponse> searchHoaDon(HoaDonFilterRequest filter, Pageable pageable) {
        Specification<HoaDon> spec = HoaDonSpecification.filter(filter);
        return hoaDonRepository.findAll(spec, pageable).map(HoaDonResponse::new);
    }

    /* ================== ADMIN: DETAIL BY ID (+ lịch sử) ================== */
    @Transactional(readOnly = true)
    public HoaDonDetailResponse getHoaDonDetail(UUID id) {
        HoaDon hoaDon = hoaDonRepository.findByIdWithPhieuGiamGia(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        // chạm vào danhSachDiaChi để tránh Lazy error nếu phía Response còn truy cập
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

        HoaDon hoaDon = hoaDonRepository.findByMa(code.trim())
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

        Integer trangThaiMoi = request.getTrangThai();

        // Nếu FE không gửi hoặc gửi rỗng -> giữ nguyên giá trị cũ
        String sdtMoi = (request.getSdt() == null || request.getSdt().trim().isEmpty())
                ? sdtCu
                : normalizePhone(request.getSdt());

        String diaChiMoi = (request.getDiaChiGiaoHang() == null || request.getDiaChiGiaoHang().trim().isEmpty())
                ? diaChiCu
                : normalizeText(request.getDiaChiGiaoHang());

        String diaChiFinal = diaChiMoi;
        String loaiHD = hoaDon.getLoaiHoaDon();
        boolean isOnline = loaiHD != null && "online".equalsIgnoreCase(loaiHD.trim());

        if (isOnline && (diaChiFinal == null || diaChiFinal.isBlank())) {
            KhachHang kh = hoaDon.getKhachHang();

            // Nếu hóa đơn có gắn với khách hàng (không phải khách vãng lai null)
            if (kh != null) {
                // Tìm địa chỉ mặc định (hoặc lấy đại cái đầu tiên) trong bảng DiaChi
                // Giả sử repo có hàm findMacDinhByKhachHangId
                List<DiaChi> listDiaChi = diaChiRepository.findByKhachHangId(kh.getId());

                if (!listDiaChi.isEmpty()) {
                    // Ưu tiên lấy cái mặc định, nếu không có mặc định thì lấy cái đầu tiên
                    DiaChi dcChon = listDiaChi.stream()
                            .filter(d -> Boolean.TRUE.equals(d.getMacDinh()))
                            .findFirst()
                            .orElse(listDiaChi.get(0));

                    // Ghép thành chuỗi (vì bảng HoaDon lưu chuỗi full)
                    // Bạn cần check null từng trường để tránh in ra chữ "null"
                    String diaChiFull = String.format("%s, %s, %s, %s",
                            dcChon.getDiaChiCuThe() == null ? "" : dcChon.getDiaChiCuThe(),
                            dcChon.getXa() == null ? "" : dcChon.getXa(),
                            dcChon.getHuyen() == null ? "" : dcChon.getHuyen(),
                            dcChon.getThanhPho() == null ? "" : dcChon.getThanhPho()
                    );

                    // Làm sạch dấu phẩy thừa (nếu có trường null)
                    diaChiFinal = diaChiFull.replaceAll(" ,", "").trim();
                    if (diaChiFinal.startsWith(",")) diaChiFinal = diaChiFinal.substring(1).trim();

                    // Cập nhật SĐT luôn nếu SĐT hóa đơn đang trống mà khách có SĐT
                    if ((sdtMoi == null || sdtMoi.isBlank()) && kh.getSdt() != null) {
                        sdtMoi = kh.getSdt();
                    }
                }
            }
        }

        if (isOnline && (diaChiFinal == null || diaChiFinal.isBlank())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Đơn hàng Online bắt buộc phải có địa chỉ giao hàng!"
            );
        }

        boolean wasConfirmedOrLater =
                trangThaiCu != null && trangThaiCu >= HoaDonStatus.DA_XAC_NHAN;
        boolean nowConfirmedOrLater =
                trangThaiMoi != null && trangThaiMoi >= HoaDonStatus.DA_XAC_NHAN;
        boolean needTruKho = !wasConfirmedOrLater && nowConfirmedOrLater;

        hoaDon.setTrangThai(trangThaiMoi);
        hoaDon.setSdt(sdtMoi);
        hoaDon.setDiaChiGiaoHang(diaChiFinal);

        HoaDon saved = hoaDonRepository.save(hoaDon);

        if (needTruKho) {
            truKhoChoHoaDon(saved);
        }

        String actor = "Admin"; // Giá trị mặc định phòng hờ

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName(); // Lấy username hoặc email từ token

            // Truy vấn DB để lấy tên thật
            // Lưu ý: Thay 'findByMa' bằng hàm tìm kiếm bạn đang có (ví dụ: findByEmail, findByTaiKhoan...)
            NhanVien nhanVien = nhanVienRepository.findByTaiKhoan(username)
                    .orElse(null);

            if (nhanVien != null) {
                actor = nhanVien.getHoTen(); // ✅ Lấy được tên thật: "Nguyễn Văn A"
            } else {
                actor = username; // Fallback: Nếu không tìm thấy thì dùng username
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
        if (!Objects.equals(diaChiCu, diaChiFinal)) {
            saveHistory(saved, actor, "Đổi địa chỉ: " + nullToDash(diaChiCu) + " -> " + nullToDash(diaChiFinal));
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

        // ✅ MẶC ĐỊNH: CHỜ THANH TOÁN (0)
        hd.setTrangThai(HoaDonStatus.CHO_THANH_TOAN);

        hd.setTenKhachHang(req.getHoTen());
        hd.setEmailKhachHang(req.getEmail());
        hd.setSdt(normalizePhone(req.getSdt()));
        hd.setDiaChiGiaoHang(normalizeText(req.getDiaChi()));
        hd.setGhiChu(normalizeText(req.getGhiChu()));

        BigDecimal tamTinh = safe(req.getTamTinh());
        BigDecimal phiShip = safe(req.getPhiVanChuyen());
        BigDecimal giamGia = safe(req.getGiamGia());
        BigDecimal tong = safe(req.getTongTien());

        if (tong.compareTo(BigDecimal.ZERO) <= 0) {
            tong = tamTinh.add(phiShip).subtract(giamGia.max(BigDecimal.ZERO));
        }
        hd.setTongTien(tong);

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

        // ghi lịch sử: trạng thái khởi tạo là CHỜ THANH TOÁN
        saveHistory(hoaDonSaved, "SYSTEM", "Tạo đơn hàng ONLINE (Chờ thanh toán)");

        try {
            mailService.sendOrderConfirmation(hoaDonSaved, savedItems);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // trả detail + items + history rỗng (vì vừa tạo lịch sử 1 bản ghi, nếu muốn trả luôn thì query lại)
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

        int st = hd.getTrangThai() == null ? HoaDonStatus.CHO_THANH_TOAN : hd.getTrangThai();

        // chỉ cho hủy khi: CHO_THANH_TOAN hoặc CHO_XAC_NHAN
        boolean allowCancel = (st == HoaDonStatus.CHO_THANH_TOAN || st == HoaDonStatus.CHO_XAC_NHAN);
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
        // Nếu trước đó chưa trừ kho (chưa xác nhận) => không cần hoàn kho.
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

    private String normalizePhone(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
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

    // ====== pick img ======
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

    /**
     * Tương thích với HoaDonDetailResponse có constructor:
     * - (HoaDon, List<HoaDonChiTietResponse>, List<LichSuHoaDon>)
     * - hoặc (HoaDon, List<HoaDonChiTietResponse>)
     */
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