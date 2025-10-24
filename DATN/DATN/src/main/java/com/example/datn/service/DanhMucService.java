package com.example.datn.service;

import com.example.datn.entity.DanhMuc;
import com.example.datn.repository.DanhMucRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class DanhMucService {

    private final DanhMucRepository repo;

    public DanhMucService(DanhMucRepository repo) {
        this.repo = repo;
    }

    public List<DanhMuc> getAll() {
        return repo.findAll();
    }

    public DanhMuc getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public DanhMuc create(DanhMuc dm) {
        return repo.save(dm);
    }

    public DanhMuc update(UUID id, DanhMuc dm) {
        dm.setId(id);
        return repo.save(dm);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
