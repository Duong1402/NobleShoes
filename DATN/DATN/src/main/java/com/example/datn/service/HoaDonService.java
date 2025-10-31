package com.example.datn.service;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.model.Response.HoaDonChiTietResponse;
import com.example.datn.model.Response.HoaDonDetailResponse;
import com.example.datn.model.Response.HoaDonResponse;
import com.example.datn.model.request.HoaDonFilterRequest;
import com.example.datn.model.request.UpdateHoaDonRequest;
import com.example.datn.repository.HoaDonChiTietRepository;
import com.example.datn.repository.HoaDonRepository;
import com.example.datn.specification.HoaDonSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

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


        return new HoaDonDetailResponse(hoaDon, chiTietResponses);
    }

    @Transactional
    public HoaDonResponse updateHoaDon(UUID id, UpdateHoaDonRequest request) {

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        hoaDon.setTrangThai(request.getTrangThai());
        hoaDon.setSdt(request.getSdt());
        hoaDon.setDiaChiGiaoHang(request.getDiaChiGiaoHang());

        HoaDon updatedHoaDon = hoaDonRepository.save(hoaDon);

        return new HoaDonResponse(updatedHoaDon);
    }


}