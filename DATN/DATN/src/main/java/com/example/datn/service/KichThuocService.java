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

    public KichThuoc create(KichThuoc kichThuoc) {
        // ⚠️ Kiểm tra và sinh mã nếu nó đang là null (tức là request thêm mới từ FE)
        if (kichThuoc.getMa() == null || kichThuoc.getMa().trim().isEmpty()) {
            // Ví dụ logic tự sinh mã: KT + 6 ký tự ngẫu nhiên từ UUID
            String newMa = "KT" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            kichThuoc.setMa(newMa);
        }

        // Đảm bảo các validation khác vẫn được xử lý (nếu cần)

        return repo.save(kichThuoc);
    }

    public KichThuoc update(UUID id, KichThuoc kt) {
        kt.setId(id);
        return repo.save(kt);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
