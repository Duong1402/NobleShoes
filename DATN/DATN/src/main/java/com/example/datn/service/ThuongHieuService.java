package com.example.datn.service;

import com.example.datn.entity.ThuongHieu;
import com.example.datn.repository.ThuongHieuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class ThuongHieuService {

    private final ThuongHieuRepository repo;

    public ThuongHieuService(ThuongHieuRepository repo) {
        this.repo = repo;
    }

    public List<ThuongHieu> getAll() {
        return repo.findAll();
    }

    public ThuongHieu getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public ThuongHieu create(ThuongHieu th) {
        return repo.save(th);
    }

    public ThuongHieu update(UUID id, ThuongHieu th) {
        th.setId(id);
        return repo.save(th);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
