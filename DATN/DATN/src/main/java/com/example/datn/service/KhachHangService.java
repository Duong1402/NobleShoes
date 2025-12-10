package com.example.datn.service;

import com.example.datn.entity.KhachHang;
import com.example.datn.repository.KhachHangRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class KhachHangService {

    private final KhachHangRepository repo;

    public KhachHangService(KhachHangRepository repo) {
        this.repo = repo;
    }

    public List<KhachHang> findAll() {
        return repo.findAll();
    }

    public Optional<KhachHang> findById(UUID id) {
        return repo.findById(id);
    }

    public KhachHang save(KhachHang obj) {
        return repo.save(obj);
    }

    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

    // ✅ đúng signature controller đang gọi
    public Page<KhachHang> findAllPage(int page, int size, String sortBy) {
        int p = Math.max(page, 0);
        int s = Math.max(size, 1);
        String field = (sortBy == null || sortBy.trim().isEmpty()) ? "ma" : sortBy.trim();

        Set<String> allowedSortFields = Set.of("ma", "hoTen", "sdt", "email", "id");
        if (!allowedSortFields.contains(field)) field = "ma";

        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, field));
        return repo.findAll(pageable);
    }
}
