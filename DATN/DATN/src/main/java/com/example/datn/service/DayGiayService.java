package com.example.datn.service;

import com.example.datn.entity.DayGiay;
import com.example.datn.repository.DayGiayRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class DayGiayService {

    private final DayGiayRepository repo;

    public DayGiayService(DayGiayRepository repo) {
        this.repo = repo;
    }

    public List<DayGiay> getAll() {
        return repo.findAll();
    }

    public DayGiay getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public DayGiay create(DayGiay dg) {
        return repo.save(dg);
    }

    public DayGiay update(UUID id, DayGiay dg) {
        dg.setId(id);
        return repo.save(dg);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
