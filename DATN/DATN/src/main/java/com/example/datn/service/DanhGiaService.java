package com.example.datn.service;

import com.example.datn.entity.DanhGia;
import com.example.datn.repository.DanhGiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DanhGiaService {
    private final DanhGiaRepository repo;

    public DanhGiaService(DanhGiaRepository repo) { this.repo = repo; }

    public List<DanhGia> findAll() { return repo.findAll(); }
    public Optional<DanhGia> findById(UUID id) { return repo.findById(id); }
    public DanhGia save(DanhGia obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
