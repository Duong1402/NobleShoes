package com.example.datn.service;

import com.example.datn.entity.GioHang;
import com.example.datn.repository.GioHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GioHangService {
    private final GioHangRepository repo;

    public GioHangService(GioHangRepository repo) { this.repo = repo; }

    public List<GioHang> findAll() { return repo.findAll(); }
    public Optional<GioHang> findById(UUID id) { return repo.findById(id); }
    public GioHang save(GioHang obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
