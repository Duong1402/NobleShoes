package com.example.datn.service;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.entity.LichSuHoaDon;
import com.example.datn.model.Response.HoaDonChiTietResponse;
import com.example.datn.model.Response.HoaDonDetailResponse;
import com.example.datn.model.Response.HoaDonResponse;
import com.example.datn.model.Response.LichSuHoaDonResponse;
import com.example.datn.model.request.HoaDonFilterRequest;
import com.example.datn.model.request.UpdateHoaDonRequest;
import com.example.datn.repository.HoaDonChiTietRepository;
import com.example.datn.repository.HoaDonRepository;
import com.example.datn.repository.LichSuHoaDonRepository;
import com.example.datn.specification.HoaDonSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    private static final Map<Integer, String> TRANG_THAI_MAP = Map.of(
            0, "Đã hủy",
            1, "Chờ xác nhận",
            2, "Đã xác nhận",
            3, "Chờ thanh toán",
            4, "Đang giao",
            5, "Hoàn thành"

    );
    public Page<HoaDonResponse> searchHoaDon(HoaDonFilterRequest filter, Pageable pageable) {
        Specification<HoaDon> spec = HoaDonSpecification.filter(filter);

        Page<HoaDon> hoaDonPage = hoaDonRepository.findAll(spec, pageable);


        return hoaDonPage.map(HoaDonResponse::new);
    }

    @Transactional(readOnly = true)
    public HoaDonDetailResponse getHoaDonDetail(UUID id) {

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));


        List<HoaDonChiTiet> chiTiets = hoaDonChiTietRepository.findAllByHoaDonId(id);


        List<HoaDonChiTietResponse> chiTietResponses = chiTiets.stream()
                .map(HoaDonChiTietResponse::new)
                .collect(Collectors.toList());

        List<LichSuHoaDon> lichSuList = lichSuHoaDonRepository.findAllByHoaDonId(id);


        List<LichSuHoaDonResponse> lichSuResponses = lichSuList.stream()
                .map(LichSuHoaDonResponse::new)
                .collect(Collectors.toList());

        return new HoaDonDetailResponse(hoaDon, chiTietResponses);
    }

    @Transactional
    public HoaDonResponse updateHoaDon(UUID id, UpdateHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));


        Integer trangThaiCu = hoaDon.getTrangThai();
        Integer trangThaiMoi = request.getTrangThai();
        String sdtCu = hoaDon.getSdt();
        String sdtMoi = request.getSdt();
        String diaChiCu = hoaDon.getDiaChiGiaoHang();
        String diaChiMoi = request.getDiaChiGiaoHang();

        String nguoiThucHien = "Admin";
        boolean coThayDoi = false;

        if (trangThaiMoi != null && !trangThaiMoi.equals(trangThaiCu)) {
            LichSuHoaDon ls = new LichSuHoaDon();
            ls.setHoaDon(hoaDon);
            ls.setThoiGian(LocalDateTime.now());
            ls.setNguoiThucHien(nguoiThucHien);
            ls.setGhiChu("Đổi trạng thái: " +
                    TRANG_THAI_MAP.getOrDefault(trangThaiCu, "Cũ") + " -> " +
                    TRANG_THAI_MAP.getOrDefault(trangThaiMoi, "Mới"));
            lichSuHoaDonRepository.save(ls);

            hoaDon.setTrangThai(trangThaiMoi);
            coThayDoi = true;
        }

        if (sdtMoi != null && !sdtMoi.equals(sdtCu)) {
            LichSuHoaDon ls = new LichSuHoaDon();
            ls.setHoaDon(hoaDon);
            ls.setThoiGian(LocalDateTime.now());
            ls.setNguoiThucHien(nguoiThucHien);
            ls.setGhiChu("Đổi SĐT: " + sdtCu + " -> " + sdtMoi);
            lichSuHoaDonRepository.save(ls);

            hoaDon.setSdt(sdtMoi);
            coThayDoi = true;
        }


        if (diaChiMoi != null && !diaChiMoi.equals(diaChiCu)) {
            LichSuHoaDon ls = new LichSuHoaDon();
            ls.setHoaDon(hoaDon);
            ls.setThoiGian(LocalDateTime.now());
            ls.setNguoiThucHien(nguoiThucHien);
            ls.setGhiChu("Đổi địa chỉ: " + diaChiCu + " -> " + diaChiMoi);
            lichSuHoaDonRepository.save(ls);

            hoaDon.setDiaChiGiaoHang(diaChiMoi);
            coThayDoi = true;
        }

        HoaDon updatedHoaDon = hoaDon;
        if (coThayDoi) {
            updatedHoaDon = hoaDonRepository.save(hoaDon);
        }


        return new HoaDonResponse(updatedHoaDon);
    }


}