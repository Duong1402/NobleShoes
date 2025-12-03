package com.example.datn.service;

import com.example.datn.entity.KichThuoc;
import com.example.datn.repository.KichThuocRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KichThuocService {

    private final KichThuocRepository repo;

    public KichThuocService(KichThuocRepository repo) {
        this.repo = repo;
    }

    public List<KichThuoc> getAll() {
        return repo.findAll();
    }

    public KichThuoc getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public KichThuoc create(KichThuoc kt) {
        return repo.save(kt);
    }

    public KichThuoc update(UUID id, KichThuoc kt) {
        kt.setId(id);
        return repo.save(kt);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
