package com.example.datn.service;

import com.example.datn.entity.GioHangChiTiet;
import com.example.datn.repository.GioHangChiTietRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GioHangChiTietService {
    private final GioHangChiTietRepository repo;

    public GioHangChiTietService(GioHangChiTietRepository repo) { this.repo = repo; }

    public List<GioHangChiTiet> findAll() { return repo.findAll(); }
    public Optional<GioHangChiTiet> findById(UUID id) { return repo.findById(id); }
    public GioHangChiTiet save(GioHangChiTiet obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
