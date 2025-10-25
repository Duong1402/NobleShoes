package com.example.datn.service;

import com.example.datn.entity.XuatXu;
import com.example.datn.repository.XuatXuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class XuatXuService {

    private final XuatXuRepository repo;

    public XuatXuService(XuatXuRepository repo) {
        this.repo = repo;
    }

    public List<XuatXu> getAll() {
        return repo.findAll();
    }

    public XuatXu getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public XuatXu create(XuatXu xx) {
        return repo.save(xx);
    }

    public XuatXu update(UUID id, XuatXu xx) {
        xx.setId(id);
        return repo.save(xx);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
