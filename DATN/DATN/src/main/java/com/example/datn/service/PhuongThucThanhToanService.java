package com.example.datn.service;

import com.example.datn.entity.PhuongThucThanhToan;
import com.example.datn.repository.PhuongThucThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PhuongThucThanhToanService {

    @Autowired
    private PhuongThucThanhToanRepository repo;

    public PhuongThucThanhToanService(PhuongThucThanhToanRepository repo) {
        this.repo = repo;
    }

    public List<PhuongThucThanhToan> findAll() {
        return repo.findAll();
    }

    public PhuongThucThanhToan save(PhuongThucThanhToan entity) {
        return repo.save(entity);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
