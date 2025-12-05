// src/main/java/com/example/datn/service/HoaDonService.java
package com.example.datn.service;

import com.example.datn.common.HoaDonStatus;
import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.model.Response.HoaDonChiTietResponse;
import com.example.datn.model.Response.HoaDonDetailResponse;
import com.example.datn.model.Response.HoaDonResponse;
import com.example.datn.model.request.CancelOrderRequest;
import com.example.datn.model.request.HoaDonFilterRequest;
import com.example.datn.model.request.TaoHoaDonRequest;
import com.example.datn.model.request.UpdateHoaDonRequest;
import com.example.datn.repository.ChiTietSanPhamRepository;
import com.example.datn.repository.HoaDonChiTietRepository;
import com.example.datn.repository.HoaDonRepository;
import com.example.datn.specification.HoaDonSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private MailService mailService;

    /* ================== ADMIN: SEARCH ================== */
    public Page<HoaDonResponse> searchHoaDon(HoaDonFilterRequest filter, Pageable pageable) {
        Specification<HoaDon> spec = HoaDonSpecification.filter(filter);
        Page<HoaDon> hoaDonPage = hoaDonRepository.findAll(spec, pageable);
        return hoaDonPage.map(HoaDonResponse::new);
    }

    /* ================== ADMIN: DETAIL BY ID ================== */
    @Transactional(readOnly = true)
    public HoaDonDetailResponse getHoaDonDetail(UUID id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        List<HoaDonChiTiet> chiTietList =
                hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());

        List<HoaDonChiTietResponse> chiTietResponses = chiTietList.stream()
                .map(ct -> {
                    HoaDonChiTietResponse r = new HoaDonChiTietResponse(ct);
                    r.setUrlAnh1(pickImg(ct.getChiTietSanPham()));
                    return r;
                })
                .collect(Collectors.toList());

        return new HoaDonDetailResponse(hoaDon, chiTietResponses);
    }

    /* ================== PUBLIC: DETAIL BY CODE ================== */
    @Transactional(readOnly = true)
    public HoaDonDetailResponse getHoaDonDetailByCode(String code) {
        return hoaDonRepository.findByMa(code)
                .map(hoaDon -> {
                    List<HoaDonChiTiet> chiTietList =
                            hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());

                    List<HoaDonChiTietResponse> chiTietResponses = chiTietList.stream()
                            .map(ct -> {
                                HoaDonChiTietResponse r = new HoaDonChiTietResponse(ct);
                                r.setUrlAnh1(pickImg(ct.getChiTietSanPham()));
                                return r;
                            })
                            .collect(Collectors.toList());

                    return new HoaDonDetailResponse(hoaDon, chiTietResponses);
                })
                .orElse(null);
    }

    /* ================== CUSTOMER: LIST MY ORDERS (BY EMAIL) ✅ THÊM MỚI ================== */
    @Transactional(readOnly = true)
    public List<HoaDonResponse> getMyOrdersByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        List<HoaDon> list = hoaDonRepository
                .findAllByEmailKhachHangIgnoreCaseOrderByNgayTaoDesc(email.trim());

        return list.stream().map(HoaDonResponse::new).collect(Collectors.toList());
    }

    /* ================== ADMIN: UPDATE ================== */
    @Transactional
    public HoaDonResponse updateHoaDon(UUID id, UpdateHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        Integer oldStatus = hoaDon.getTrangThai();
        Integer newStatus = request.getTrangThai();

        hoaDon.setTrangThai(newStatus);
        hoaDon.setSdt(request.getSdt());
        hoaDon.setDiaChiGiaoHang(request.getDiaChiGiaoHang());

        boolean wasConfirmedOrLater = (oldStatus != null && oldStatus >= HoaDonStatus.DA_XAC_NHAN);
        boolean nowConfirmedOrLater = (newStatus != null && newStatus >= HoaDonStatus.DA_XAC_NHAN);

        boolean needTruKho = !wasConfirmedOrLater && nowConfirmedOrLater;

        HoaDon updatedHoaDon = hoaDonRepository.save(hoaDon);

        if (needTruKho) {
            truKhoChoHoaDon(updatedHoaDon);
        }

        return new HoaDonResponse(updatedHoaDon);
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
        hd.setTrangThai(HoaDonStatus.CHO_XAC_NHAN);

        hd.setTenKhachHang(req.getHoTen());
        hd.setEmailKhachHang(req.getEmail());
        hd.setSdt(req.getSdt());
        hd.setDiaChiGiaoHang(req.getDiaChi());
        hd.setGhiChu(req.getGhiChu());

        BigDecimal tamTinh = safe(req.getTamTinh());
        BigDecimal phiShip = safe(req.getPhiVanChuyen());
        BigDecimal giamGia = safe(req.getGiamGia());
        BigDecimal tong = safe(req.getTongTien());

        if (tong.compareTo(BigDecimal.ZERO) <= 0) {
            tong = tamTinh.add(phiShip).subtract(giamGia.max(BigDecimal.ZERO));
        }
        hd.setTongTien(tong);

        HoaDon hoaDonSaved = hoaDonRepository.save(hd);

        List<HoaDonChiTietResponse> chiTietResponses = new ArrayList<>();
        List<HoaDonChiTiet> savedItems = new ArrayList<>();

        if (req.getItems() != null) {
            for (TaoHoaDonRequest.Item item : req.getItems()) {
                HoaDonChiTiet ct = new HoaDonChiTiet();
                ct.setHoaDon(hoaDonSaved);

                ChiTietSanPham chiTietSanPham = null;
                if (item.getChiTietSanPhamId() != null) {
                    chiTietSanPham = chiTietSanPhamRepository
                            .findById(item.getChiTietSanPhamId())
                            .orElse(null);
                }
                ct.setChiTietSanPham(chiTietSanPham);

                ct.setTenSanPham(item.getTenSanPham());
                ct.setSize(item.getSize());

                int soLuong = item.getSoLuong() == null ? 1 : item.getSoLuong();
                ct.setSoLuong(soLuong);

                BigDecimal donGia = item.getDonGia() != null
                        ? item.getDonGia()
                        : (chiTietSanPham != null && chiTietSanPham.getGiaBan() != null
                        ? chiTietSanPham.getGiaBan()
                        : BigDecimal.ZERO);

                ct.setDonGia(donGia);
                ct.setThanhTien(donGia.multiply(BigDecimal.valueOf(soLuong)));
                ct.setTrangThai(1);

                HoaDonChiTiet savedCt = hoaDonChiTietRepository.save(ct);
                savedItems.add(savedCt);

                HoaDonChiTietResponse r = new HoaDonChiTietResponse(savedCt);
                r.setUrlAnh1(pickImg(chiTietSanPham));
                chiTietResponses.add(r);
            }
        }

        try {
            mailService.sendOrderConfirmation(hoaDonSaved, savedItems);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new HoaDonDetailResponse(hoaDonSaved, chiTietResponses);
    }

    /* ================== PUBLIC: CANCEL ORDER BY CODE (NO LOGIN) ✅ THÊM MỚI ================== */
    @Transactional
    public void cancelPublicByCode(String code, CancelOrderRequest req) {
        if (code == null || code.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu mã đơn hàng");
        }

        HoaDon hd = hoaDonRepository.findByMa(code.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng"));

        Integer st = hd.getTrangThai();
        if (st == null) st = HoaDonStatus.CHO_XAC_NHAN;

        // ✅ chỉ cho hủy khi: 0 (Chờ thanh toán) hoặc CHO_XAC_NHAN
        boolean allowCancel = (st == 0 || st == HoaDonStatus.CHO_XAC_NHAN);

        // Nếu bạn có constant CHO_THANH_TOAN thì mở thêm dòng này:
        // allowCancel = allowCancel || st == HoaDonStatus.CHO_THANH_TOAN;

        if (!allowCancel) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Đơn hàng không thể hủy ở trạng thái hiện tại");
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

        // ✅ 5 = Đã hủy (theo FE map của bạn)
        hd.setTrangThai(5);

        // (tuỳ bạn) lưu lý do vào ghi chú nếu muốn
        // String reason = (req != null ? req.getReason() : null);
        // if (reason != null && !reason.isBlank()) {
        //     String old = hd.getGhiChu() == null ? "" : hd.getGhiChu();
        //     hd.setGhiChu((old.isBlank() ? "" : (old + " | ")) + "[HỦY] " + reason.trim());
        // }

        hoaDonRepository.save(hd);

        // NOTE: Vì chỉ cho hủy ở trạng thái 0/CHO_XAC_NHAN => chưa trừ kho => không cần hoàn kho.
    }

    /* ================== PRIVATE HELPERS ================== */

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String pickImg(ChiTietSanPham ctsp) {
        if (ctsp == null) return null;

        String raw = firstNonBlank(
                readString(ctsp, "urlAnh1", "urlAnh", "anh1", "anh", "image", "hinhAnhUrl", "anhSanPham", "duongDanAnh"),
                readStringNested(ctsp, "hinhAnh", "urlAnh1"),
                readStringNested(ctsp, "hinhAnh", "urlAnh"),
                readStringNested(ctsp, "sanPham", "urlAnh1"),
                readStringNested(ctsp, "sanPham", "hinhAnh", "urlAnh1"),
                readStringNested(ctsp, "sanPham", "hinhAnh", "urlAnh")
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
                String s = ((String) v).trim();
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
            String s = ((String) cur).trim();
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
        } catch (Exception ignored) {}

        try {
            Method m = c.getMethod("is" + up);
            return m.invoke(obj);
        } catch (Exception ignored) {}

        try {
            Field f = c.getDeclaredField(prop);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception ignored) {}

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

    private void truKhoChoHoaDon(HoaDon hoaDon) {
        List<HoaDonChiTiet> chiTietList =
                hoaDonChiTietRepository.findAllByHoaDon_Id(hoaDon.getId());

        if (chiTietList.isEmpty()) return;

        for (HoaDonChiTiet cthd : chiTietList) {
            ChiTietSanPham ctsp = cthd.getChiTietSanPham();
            if (ctsp == null) continue;

            int soLuongDat = cthd.getSoLuong() == null ? 0 : cthd.getSoLuong();
            int tonHienTai = ctsp.getSoLuongTon() == null ? 0 : ctsp.getSoLuongTon();

            if (soLuongDat <= 0) continue;

            if (tonHienTai < soLuongDat) {
                throw new RuntimeException("Không đủ số lượng hàng trong kho cho sản phẩm: " +
                        (cthd.getTenSanPham() != null ? cthd.getTenSanPham() : "Không rõ"));
            }

            ctsp.setSoLuongTon(tonHienTai - soLuongDat);
            chiTietSanPhamRepository.save(ctsp);
        }
    }
}
