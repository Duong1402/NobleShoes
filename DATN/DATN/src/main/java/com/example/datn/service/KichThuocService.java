package com.example.datn.service;

import com.example.datn.entity.KichThuoc;
import com.example.datn.repository.KichThuocRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KichThuocService {

    private final KichThuocRepository repo;

    public KichThuocService(KichThuocRepository repo) {
        this.repo = repo;
    }

    public List<KichThuoc> getAll() {
        return repo.findAll();
    }

    public KichThuoc getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    /**
     * Create: nếu FE không gửi "ma" thì tự sinh mã KT + 6 ký tự
     */
    public KichThuoc create(KichThuoc kt) {
        if (kt.getMa() == null || kt.getMa().trim().isEmpty()) {
            String newMa = "KT" + UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 6)
                    .toUpperCase();
            kt.setMa(newMa);
        }
        return repo.save(kt);
    }

    /**
     * Update: set id theo path param rồi save
     * (giữ nguyên logic như 2 bản của bạn)
     */
    public KichThuoc update(UUID id, KichThuoc kt) {
        kt.setId(id);
        return repo.save(kt);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
