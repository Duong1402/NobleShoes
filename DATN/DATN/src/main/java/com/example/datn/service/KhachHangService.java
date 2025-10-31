package com.example.datn.service;

import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KhachHangService {
    private final KhachHangRepository repo;

    public KhachHangService(KhachHangRepository repo) { this.repo = repo; }

    public List<KhachHang> findAll() { return repo.findAll(); }
    public Optional<KhachHang> findById(UUID id) { return repo.findById(id); }
    public KhachHang save(KhachHang obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}