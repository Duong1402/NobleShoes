package com.example.datn.service;

import com.example.datn.entity.LienHe;
import com.example.datn.repository.LienHeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LienHeService {
    private final LienHeRepository repo;

    public LienHeService(LienHeRepository repo) { this.repo = repo; }

    public List<LienHe> findAll() { return repo.findAll(); }
    public Optional<LienHe> findById(UUID id) { return repo.findById(id); }
    public LienHe save(LienHe obj) { return repo.save(obj); }
    public void deleteById(UUID id) { repo.deleteById(id); }
}
