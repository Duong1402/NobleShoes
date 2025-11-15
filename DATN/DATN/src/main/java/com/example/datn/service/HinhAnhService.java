package com.example.datn.service;

import com.example.datn.entity.HinhAnh;
import com.example.datn.repository.HinhAnhRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class HinhAnhService {

    private final HinhAnhRepository repo;

    public HinhAnhService(HinhAnhRepository repo) {
        this.repo = repo;
    }

    public List<HinhAnh> getAll() {
        return repo.findAll();
    }

    public HinhAnh getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public HinhAnh create(HinhAnh ha) {
        return repo.save(ha);
    }

    public HinhAnh update(UUID id, HinhAnh ha) {
        ha.setId(id);
        return repo.save(ha);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
