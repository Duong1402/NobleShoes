package com.example.datn.service;

import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.repository.PhieuGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PhieuGiamGiaService {
    @Autowired
    private PhieuGiamGiaRepository repo;


    public Page<PhieuGiamGia> findAll(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return repo.findAll(pageable);
    }

    public Optional<PhieuGiamGia> findById(UUID id) {
        return repo.findById(id);
    }

    public PhieuGiamGia save(PhieuGiamGia obj) {
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
        return String.format("PGG%03d", count);
    }
}