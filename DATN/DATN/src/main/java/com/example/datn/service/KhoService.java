package com.example.datn.service;

import com.example.datn.entity.Kho;
import com.example.datn.repository.KhoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KhoService {
    private final KhoRepository repo;

    public KhoService(KhoRepository repo) { this.repo = repo; }

    public List<Kho> findAll() { return repo.findAll(); }
    public Optional<Kho> findById(UUID id) { return repo.findById(id); }
    public Kho save(Kho obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
