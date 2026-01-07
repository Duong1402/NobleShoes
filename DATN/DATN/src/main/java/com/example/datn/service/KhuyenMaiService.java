package com.example.datn.service;

import com.example.datn.entity.KhuyenMai;
import com.example.datn.repository.KhuyenMaiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KhuyenMaiService {
    private final KhuyenMaiRepository repo;

    public KhuyenMaiService(KhuyenMaiRepository repo) { this.repo = repo; }

    public List<KhuyenMai> findAll() { return repo.findAll(); }
    public Optional<KhuyenMai> findById(UUID id) { return repo.findById(id); }
    public KhuyenMai save(KhuyenMai obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
