package com.example.datn.service;

import com.example.datn.dto.ChiTietSanPhamDTO;
import com.example.datn.dto.ChiTietSanPhamResponse;
import com.example.datn.dto.ChiTietSanPhamUpdateDTO;
import com.example.datn.entity.*;
import com.example.datn.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChiTietSanPhamService {

    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;
    private final ChatLieuRepository chatLieuRepository;
    private final HinhAnhRepository hinhAnhRepository;
    private final MucDichSuDungRepository mucDichSuDungRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final XuatXuRepository xuatXuRepository;
    private final DanhMucRepository danhMucRepository;

    // 1️⃣ Lấy tất cả chi tiết sản phẩm (entity) cho admin
    public List<ChiTietSanPham> getAllChiTietSanPham() {
        return chiTietSanPhamRepository.findAll();
    }

    // 2️⃣ Lấy chi tiết sản phẩm theo ID sản phẩm -> trả về DTO (dùng chung admin/client)
    public List<ChiTietSanPhamDTO> getChiTietSanPhamBySanPhamId(UUID sanPhamId) {
        return chiTietSanPhamRepository.findChiTietSanPhamDTOBySanPhamId(sanPhamId);
    }

    // 3️⃣ Cập nhật chi tiết sản phẩm (modal)
    @Transactional
    public ChiTietSanPham updateChiTietSanPham(UUID id, ChiTietSanPhamUpdateDTO dto) {
        ChiTietSanPham existing = chiTietSanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại!"));

        // --- cập nhật thông tin Sản phẩm ---
        if (dto.getTenSP() != null) existing.getSanPham().setTen(dto.getTenSP());

        if (dto.getDanhMucId() != null) {
            DanhMuc dm = danhMucRepository.findById(dto.getDanhMucId())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại!"));
            existing.getSanPham().setDanhMuc(dm);
        }

        if (dto.getThuongHieuId() != null) {
            ThuongHieu th = thuongHieuRepository.findById(dto.getThuongHieuId())
                    .orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại!"));
            existing.getSanPham().setThuongHieu(th);
        }

        if (dto.getXuatXuId() != null) {
            XuatXu xx = xuatXuRepository.findById(dto.getXuatXuId())
                    .orElseThrow(() -> new RuntimeException("Xuất xứ không tồn tại!"));
            existing.getSanPham().setXuatXu(xx);
        }

        if (dto.getMucDichSuDungId() != null) {
            MucDichSuDung md = mucDichSuDungRepository.findById(dto.getMucDichSuDungId())
                    .orElseThrow(() -> new RuntimeException("Mục đích sử dụng không tồn tại!"));
            existing.getSanPham().setMucDichSuDung(md);
        }

        // --- cập nhật thông tin ChiTietSanPham ---
        if (dto.getGiaBan() != null) existing.setGiaBan(dto.getGiaBan());
        if (dto.getSoLuongTon() != null) existing.setSoLuongTon(dto.getSoLuongTon());
        existing.setMoTa(dto.getMoTa());

        if (dto.getMauSacId() != null) {
            MauSac ms = mauSacRepository.findById(dto.getMauSacId())
                    .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại!"));
            existing.setMauSac(ms);
        }

        if (dto.getKichThuocId() != null) {
            KichThuoc kt = kichThuocRepository.findById(dto.getKichThuocId())
                    .orElseThrow(() -> new RuntimeException("Kích thước không tồn tại!"));
            existing.setKichThuoc(kt);
        }

        if (dto.getChatLieuId() != null) {
            ChatLieu cl = chatLieuRepository.findById(dto.getChatLieuId())
                    .orElseThrow(() -> new RuntimeException("Chất liệu không tồn tại!"));
            existing.setChatLieu(cl);
        }

        // --- hình ảnh (gắn vào SanPham.hinhAnh) ---
        if (dto.getHinhAnhUrl() != null && !dto.getHinhAnhUrl().isEmpty()) {
            HinhAnh ha = existing.getSanPham().getHinhAnh();
            if (ha == null) {
                ha = new HinhAnh();
                ha.setMa("HA-" + UUID.randomUUID().toString().substring(0, 8));
                ha = hinhAnhRepository.save(ha);
                existing.getSanPham().setHinhAnh(ha);
            }

            if (ha.getUrlAnh1() == null || ha.getUrlAnh1().isEmpty()) ha.setUrlAnh1(dto.getHinhAnhUrl());
            else if (ha.getUrlAnh2() == null || ha.getUrlAnh2().isEmpty()) ha.setUrlAnh2(dto.getHinhAnhUrl());
            else ha.setUrlAnh3(dto.getHinhAnhUrl());

            hinhAnhRepository.save(ha);
        }

        return chiTietSanPhamRepository.saveAndFlush(existing);
    }

    // 4️⃣ Inline update giá bán & số lượng tồn (dùng cho table)
    @Transactional
    public void updateGiaBanVaSoLuong(UUID ctspId, BigDecimal giaBan, Integer soLuongTon) {
        ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ctspId)
                .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại!"));

        if (giaBan != null && giaBan.compareTo(BigDecimal.ZERO) > 0) ctsp.setGiaBan(giaBan);
        if (soLuongTon != null && soLuongTon >= 0) ctsp.setSoLuongTon(soLuongTon);

        chiTietSanPhamRepository.save(ctsp);
    }

    // 5️⃣ API trả về response cho admin list (nếu controller đang gọi getAll())
    public List<ChiTietSanPhamResponse> getAll() {
        return chiTietSanPhamRepository.findAllOrderByMaDescNative()
                .stream()
                .map(ChiTietSanPhamResponse::new)
                .collect(Collectors.toList());
    }

}
