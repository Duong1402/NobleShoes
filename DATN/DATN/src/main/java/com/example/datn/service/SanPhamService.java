package com.example.datn.service;

import com.example.datn.dto.ChiTietSanPhamRequest;
import com.example.datn.dto.SanPhamRequest;
import com.example.datn.entity.*;
import com.example.datn.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final HinhAnhRepository hinhAnhRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final DanhMucRepository danhMucRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final MucDichSuDungRepository mucDichSuDungRepository;
    private final DeGiayRepository deGiayRepository;
    private final DayGiayRepository dayGiayRepository;
    private final XuatXuRepository xuatXuRepository;

    @Transactional
    public SanPham saveSanPham(SanPhamRequest req) {

        // ✅ Giữ chức năng bản 1: Check trùng tên sản phẩm
        if (sanPhamRepository.existsByTen(req.getTen())) {
            throw new RuntimeException("Lỗi: Tên sản phẩm đã tồn tại!");
        }

        // 1) Lưu Hình ảnh cho sản phẩm (lấy từ biến thể đầu tiên nếu có)
        HinhAnh hinhAnh = new HinhAnh();
        if (req.getChiTietSanPham() != null && !req.getChiTietSanPham().isEmpty()) {
            List<String> urls = req.getChiTietSanPham().get(0).getImages();
            if (urls != null) {
                if (urls.size() > 0) hinhAnh.setUrlAnh1(urls.get(0));
                if (urls.size() > 1) hinhAnh.setUrlAnh2(urls.get(1));
                if (urls.size() > 2) hinhAnh.setUrlAnh3(urls.get(2));
            }
        }
        hinhAnh.setMa("HA" + System.currentTimeMillis());
        hinhAnhRepository.save(hinhAnh);

        // 2) Lưu Sản phẩm
        SanPham sp = new SanPham();
        sp.setTen(req.getTen());
        sp.setMa(req.getMa() != null ? req.getMa() : "SP" + System.currentTimeMillis());
        sp.setTrangThai(true);
        sp.setNgayTao(LocalDate.now());
        sp.setNgayCapNhat(LocalDate.now());

        sp.setDanhMuc(danhMucRepository.findById(req.getIdDanhMuc()).orElse(null));
        sp.setThuongHieu(thuongHieuRepository.findById(req.getIdThuongHieu()).orElse(null));
        sp.setMucDichSuDung(mucDichSuDungRepository.findById(req.getIdMucDichSuDung()).orElse(null));
        sp.setDeGiay(deGiayRepository.findById(req.getIdDeGiay()).orElse(null));
        sp.setDayGiay(dayGiayRepository.findById(req.getIdDayGiay()).orElse(null));
        sp.setXuatXu(xuatXuRepository.findById(req.getIdXuatXu()).orElse(null));
        sp.setHinhAnh(hinhAnh);

        SanPham saved = sanPhamRepository.save(sp);

        // 3) Lưu chi tiết sản phẩm (biến thể) - KHÔNG gán HinhAnh
        List<ChiTietSanPham> chiTietList = new ArrayList<>();

        // ✅ Giữ chức năng bản 1: tự sinh mã CTSP theo max hiện có
        int currentMaxNumber = 0;
        String maxMa = chiTietSanPhamRepository.findMaxMaCTSP(); // ví dụ "CTSP09"
        if (maxMa != null && maxMa.startsWith("CTSP")) {
            try {
                currentMaxNumber = Integer.parseInt(maxMa.substring(4));
            } catch (NumberFormatException ignored) {
                currentMaxNumber = 0;
            }
        }

        if (req.getChiTietSanPham() != null) {
            for (ChiTietSanPhamRequest ctReq : req.getChiTietSanPham()) {
                currentMaxNumber++;

                ChiTietSanPham ct = new ChiTietSanPham();

                String formattedNumber = String.format("%02d", currentMaxNumber);
                ct.setMa("CTSP" + formattedNumber);

                ct.setSanPham(saved);
                ct.setGiaBan(ctReq.getGiaBan());
                ct.setSoLuongTon(ctReq.getSoLuongTon());
                ct.setMoTa(ctReq.getMoTa());
                ct.setMauSac(mauSacRepository.findById(ctReq.getIdMauSac()).orElse(null));
                ct.setKichThuoc(kichThuocRepository.findById(ctReq.getIdKichThuoc()).orElse(null));
                ct.setChatLieu(chatLieuRepository.findById(ctReq.getIdChatLieu()).orElse(null));

                chiTietList.add(ct);
            }
        }

        chiTietSanPhamRepository.saveAll(chiTietList);
        return saved;
    }

    public SanPham createSanPham(SanPham sp) {
        if (sp.getHinhAnh() == null) {
            HinhAnh hinhAnh = new HinhAnh();
            hinhAnh.setMa("HA-" + UUID.randomUUID().toString().substring(0, 8));
            sp.setHinhAnh(hinhAnh);
        }
        return sanPhamRepository.save(sp); // cascade sẽ lưu cả HinhAnh (nếu mapping có cascade)
    }

    public List<Map<String, Object>> getAll() {
        return sanPhamRepository.getDanhSachSanPhamVaChiTiet();
    }

    @Transactional
    public void updateSanPham(UUID id, SanPhamRequest req) {
        SanPham sp = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));

        sp.setTen(req.getTen());
        sp.setTrangThai(req.getTrangThai());
        sp.setNgayCapNhat(LocalDate.now());

        sp.setDanhMuc(danhMucRepository.findById(req.getIdDanhMuc()).orElse(null));
        sp.setThuongHieu(thuongHieuRepository.findById(req.getIdThuongHieu()).orElse(null));
        sp.setXuatXu(xuatXuRepository.findById(req.getIdXuatXu()).orElse(null));
        sp.setMucDichSuDung(mucDichSuDungRepository.findById(req.getIdMucDichSuDung()).orElse(null));
        sp.setDeGiay(deGiayRepository.findById(req.getIdDeGiay()).orElse(null));
        sp.setDayGiay(dayGiayRepository.findById(req.getIdDayGiay()).orElse(null));

        sanPhamRepository.save(sp);
    }

    public void updateTrangThai(UUID id, boolean newValue) {
        SanPham sp = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm!"));
        sp.setTrangThai(newValue);
        sanPhamRepository.save(sp);
    }
}
