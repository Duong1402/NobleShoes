package com.example.datn.service;

import com.example.datn.entity.HoaDonChiTiet;
import com.example.datn.repository.HoaDonChiTietRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HoaDonChiTietService {
    private final HoaDonChiTietRepository repo;

    public HoaDonChiTietService(HoaDonChiTietRepository repo) {
        this.repo = repo;
    }

    public List<HoaDonChiTiet> findAll() {
        return repo.findAll();
    }

    public Optional<HoaDonChiTiet> findById(UUID id) {
        return repo.findById(id);
    }

    public HoaDonChiTiet save(HoaDonChiTiet obj) {
        return repo.save(obj);
    }

    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}