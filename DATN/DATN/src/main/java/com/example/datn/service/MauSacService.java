package com.example.datn.service;

import com.example.datn.entity.MauSac;
import com.example.datn.repository.MauSacRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MauSacService {

    private final MauSacRepository repo;

    public MauSacService(MauSacRepository repo) {
        this.repo = repo;
    }

    public List<MauSac> getAll() {
        return repo.findAll();
    }

    public MauSac getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public MauSac create(MauSac ms) {
        return repo.save(ms);
    }

    public MauSac update(UUID id, MauSac ms) {
        ms.setId(id);
        return repo.save(ms);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
