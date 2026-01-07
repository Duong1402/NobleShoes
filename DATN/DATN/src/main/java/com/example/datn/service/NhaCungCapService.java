package com.example.datn.service;

import com.example.datn.entity.NhaCungCap;
import com.example.datn.repository.NhaCungCapRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NhaCungCapService {
    private final NhaCungCapRepository repo;

    public NhaCungCapService(NhaCungCapRepository repo) { this.repo = repo; }

    public List<NhaCungCap> findAll() { return repo.findAll(); }
    public Optional<NhaCungCap> findById(UUID id) { return repo.findById(id); }
    public NhaCungCap save(NhaCungCap obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
