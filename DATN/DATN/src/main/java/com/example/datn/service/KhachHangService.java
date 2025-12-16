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
import java.util.Set;
import java.util.UUID;

@Service
public class KhachHangService {
    private final KhachHangRepository repo;
    private final DiaChiService diaChiService;

    public KhachHangService(KhachHangRepository repo, DiaChiService diaChiService) {
        this.repo = repo;
        this.diaChiService = diaChiService;
    }

    // ==========================
    // 📌 FIND ALL + PAGINATION
    // ==========================
    public Page<KhachHang> findAllPage(int page, int size, String sortBy) {
        int p = Math.max(page, 0);
        int s = Math.max(size, 1);

        String field = (sortBy == null || sortBy.trim().isEmpty()) ? "ma" : sortBy.trim();

        Set<String> allowedSortFields = Set.of("ma", "hoTen", "sdt", "email", "id");
        if (!allowedSortFields.contains(field)) field = "ma";

        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, field));
        return repo.findAll(pageable);
    }

    public List<KhachHang> findAll() {
        return repo.findAll();
    }

    public Optional<KhachHang> findById(UUID id) {
        return repo.findById(id);
    }

    // ==========================
    // 📌 SAVE + AUTO GENERATE MA
    // ==========================
    public KhachHang save(KhachHang obj) {
        boolean isNew = (obj.getId() == null);

        if (isNew && (obj.getMa() == null || obj.getMa().trim().isEmpty())) {
            obj.setMa(generateNewMa());
        }

        return repo.save(obj);
    }

    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

    // ==========================
    // 📌 TẠO MÃ KH TỰ ĐỘNG
    // ==========================
    public String generateNewMa() {
        String maxMa = repo.findMaxMaKhachHang();
        int nextNumber = 1;

        if (maxMa != null && maxMa.startsWith("KH")) {
            try {
                String numberPart = maxMa.substring(2);
                nextNumber = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                System.err.println("Lỗi parse mã KH: " + maxMa);
                nextNumber = 1;
            }
        }

        return String.format("KH%02d", nextNumber);
    }

    // ==========================
    // 📌 TÌM THEO TÀI KHOẢN
    // ==========================
    public Optional<KhachHang> findByTaiKhoan(String tk) {
        return repo.findByTaiKhoan(tk);
    }

    // ==========================
    // 📌 CHECK EMAIL
    // ==========================
    public Boolean existsByEmail(String email) {
        return repo.existsByEmail(email);
    }
}