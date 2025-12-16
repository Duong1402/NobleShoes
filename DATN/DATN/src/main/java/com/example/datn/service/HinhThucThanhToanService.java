package com.example.datn.service;

import com.example.datn.entity.HinhThucThanhToan;
import com.example.datn.repository.HinhThucThanhToanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HinhThucThanhToanService {

    private final HinhThucThanhToanRepository repo;

    public HinhThucThanhToanService(HinhThucThanhToanRepository repo) {
        this.repo = repo;
    }

    public List<HinhThucThanhToan> findAll() {
        return repo.findAll();
    }

    public HinhThucThanhToan findById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public List<HinhThucThanhToan> findByHoaDon(UUID idHoaDon) {
        return repo.findByHoaDon_Id(idHoaDon);
    }

    public HinhThucThanhToan save(HinhThucThanhToan entity) {
        return repo.save(entity);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
