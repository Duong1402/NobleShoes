package com.example.datn.service;

import com.example.datn.entity.NhapKho;
import com.example.datn.repository.NhapKhoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NhapKhoService {
    private final NhapKhoRepository repo;

    public NhapKhoService(NhapKhoRepository repo) { this.repo = repo; }

    public List<NhapKho> findAll() { return repo.findAll(); }
    public Optional<NhapKho> findById(UUID id) { return repo.findById(id); }
    public NhapKho save(NhapKho obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
