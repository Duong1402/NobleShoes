package com.example.datn.service;

import com.example.datn.entity.SanPhamGiamGia;
import com.example.datn.repository.SanPhamGiamGiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SanPhamGiamGiaService {
    private final SanPhamGiamGiaRepository repo;

    public SanPhamGiamGiaService(SanPhamGiamGiaRepository repo) { this.repo = repo; }

    public List<SanPhamGiamGia> findAll() { return repo.findAll(); }
    public Optional<SanPhamGiamGia> findById(UUID id) { return repo.findById(id); }
    public SanPhamGiamGia save(SanPhamGiamGia obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
