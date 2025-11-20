package com.example.datn.service;

import com.example.datn.model.Response.LichSuHoaDonResponse;
import com.example.datn.repository.LichSuHoaDonRepository;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LichSuHoaDonService {
    @Autowired
    private LichSuHoaDonRepository repo;
    @Autowired
    private NhanVienRepository nhanVienRepo;

    public List<LichSuHoaDonResponse> getLichSuByHoaDonId(UUID id) {
        return repo.findAllByHoaDonId(id)
                .stream()
                .map(LichSuHoaDonResponse::new)
                .toList();
    }
}
