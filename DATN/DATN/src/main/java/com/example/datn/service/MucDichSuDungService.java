package com.example.datn.service;

import com.example.datn.entity.MucDichSuDung;
import com.example.datn.repository.MucDichSuDungRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class MucDichSuDungService {

    private final MucDichSuDungRepository repo;

    public MucDichSuDungService(MucDichSuDungRepository repo) {
        this.repo = repo;
    }

    public List<MucDichSuDung> getAll() {
        return repo.findAll();
    }

    public MucDichSuDung getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    public MucDichSuDung create(MucDichSuDung md) {
        return repo.save(md);
    }

    public MucDichSuDung update(UUID id, MucDichSuDung md) {
        md.setId(id);
        return repo.save(md);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
