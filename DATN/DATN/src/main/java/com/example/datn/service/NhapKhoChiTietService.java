package com.example.datn.service;

import com.example.datn.entity.NhapKhoChiTiet;
import com.example.datn.repository.NhapKhoChiTietRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NhapKhoChiTietService {
    private final NhapKhoChiTietRepository repo;

    public NhapKhoChiTietService(NhapKhoChiTietRepository repo) { this.repo = repo; }

    public List<NhapKhoChiTiet> findAll() { return repo.findAll(); }
    public Optional<NhapKhoChiTiet> findById(UUID id) { return repo.findById(id); }
    public NhapKhoChiTiet save(NhapKhoChiTiet obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
