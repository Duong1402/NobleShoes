package com.example.datn.service;

import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KhachHangService {
    private final KhachHangRepository repo;

    public KhachHangService(KhachHangRepository repo) {
        this.repo = repo;
    }

    public Page<KhachHang> findAllPage(int page, int size, String sortBy) {
        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return repo.findAll(pageable);
    }

    public List<KhachHang> findAll() {
        return repo.findAll();
    }

    public Optional<KhachHang> findById(UUID id) {
        return repo.findById(id);
    }

    public KhachHang save(KhachHang obj) {
        if (obj.getMa() == null || obj.getMa().isEmpty()) {
            obj.setMa(autoTaoMa());
        }
        return repo.save(obj);
    }

    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

    public Optional<KhachHang> findByTaiKhoan(String tk){
        return repo.findByTaiKhoan(tk);
    }

    public Boolean existsByEmail(String email){
        return repo.existsByEmail(email);
    }

    private String autoTaoMa() {
        long count = repo.count();
        return String.format("KH%03d", count);
    }
}