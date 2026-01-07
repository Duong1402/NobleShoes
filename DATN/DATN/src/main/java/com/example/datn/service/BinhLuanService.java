package com.example.datn.service;

import com.example.datn.entity.BinhLuan;
import com.example.datn.repository.BinhLuanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BinhLuanService {
    private final BinhLuanRepository repo;

    public BinhLuanService(BinhLuanRepository repo) { this.repo = repo; }

    public List<BinhLuan> findAll() { return repo.findAll(); }
    public Optional<BinhLuan> findById(UUID id) { return repo.findById(id); }
    public BinhLuan save(BinhLuan obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
