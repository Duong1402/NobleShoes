package com.example.datn.service;

import com.example.datn.entity.DiaChiKhachHang;
import com.example.datn.repository.DiaChiKhachHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DiaChiKhachHangService {
    private final DiaChiKhachHangRepository repo;

    public DiaChiKhachHangService(DiaChiKhachHangRepository repo) { this.repo = repo; }

    public List<DiaChiKhachHang> findAll() { return repo.findAll(); }
    public Optional<DiaChiKhachHang> findById(UUID id) { return repo.findById(id); }
    public DiaChiKhachHang save(DiaChiKhachHang obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
