package com.example.datn.service;

import com.example.datn.entity.PhieuGiamGiaCaNhan;
import com.example.datn.repository.PhieuGiamGiaCaNhanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhieuGiamGiaCaNhanService {
    @Autowired
    private PhieuGiamGiaCaNhanRepository repo;

    public List<PhieuGiamGiaCaNhan> findAll() {
        return repo.findAll();
    }

    public Optional<PhieuGiamGiaCaNhan> findById(UUID id) {
        return repo.findById(id);
    }

    public PhieuGiamGiaCaNhan save(PhieuGiamGiaCaNhan obj) {
        if (obj.getMa() == null || obj.getMa().isEmpty()) {
            obj.setMa(autoTaoMa());
        }
        return repo.save(obj);
    }

    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

    private String autoTaoMa() {
        long count = repo.count();
        return String.format("PGGCN%03d", count);
    }
}