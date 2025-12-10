package com.example.datn.service;

import com.example.datn.entity.PhuongThucThanhToan;
import com.example.datn.repository.PhuongThucThanhToanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhuongThucThanhToanService {

    private final PhuongThucThanhToanRepository repo;

    public PhuongThucThanhToanService(PhuongThucThanhToanRepository repo) {
        this.repo = repo;
    }

    public List<PhuongThucThanhToan> findAll() {
        return repo.findAll();
    }

    // ✅ method Controller đang gọi
    public Optional<PhuongThucThanhToan> findById(UUID id) {
        return repo.findById(id);
    }

    public PhuongThucThanhToan save(PhuongThucThanhToan obj) {
        return repo.save(obj);
    }

    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}
