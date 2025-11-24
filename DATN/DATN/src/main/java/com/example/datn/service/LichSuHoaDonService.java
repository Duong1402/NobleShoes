package com.example.datn.service;

import com.example.datn.entity.LichSuHoaDon;
import com.example.datn.repository.LichSuHoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LichSuHoaDonService {

    @Autowired
    private LichSuHoaDonRepository repository;

    public LichSuHoaDon save(LichSuHoaDon lichSuHoaDon) {
        // Tự sinh ID nếu chưa có
        if (lichSuHoaDon.getId() == null) {
            lichSuHoaDon.setId(UUID.randomUUID());
        }
        // Tự set thời gian hiện tại nếu chưa có
        if (lichSuHoaDon.getThoiGian() == null) {
            lichSuHoaDon.setThoiGian(LocalDateTime.now());
        }
        return repository.save(lichSuHoaDon);
    }

    public List<LichSuHoaDon> findAll() {
        return repository.findAll();
    }

    public LichSuHoaDon findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
