package com.example.datn.service;

import com.example.datn.entity.DeGiay;
import com.example.datn.repository.DeGiayRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class DeGiayService {

    private final DeGiayRepository repo;

    public DeGiayService(DeGiayRepository repo) {
        this.repo = repo;
    }

    public List<DeGiay> getAll() {
        return repo.findAll();
    }

    public DeGiay getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public DeGiay create(DeGiay dg) {
        return repo.save(dg);
    }

    public DeGiay update(UUID id, DeGiay dg) {
        dg.setId(id);
        return repo.save(dg);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
