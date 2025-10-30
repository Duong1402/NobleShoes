package com.example.datn.service;

import com.example.datn.entity.DotGiamGia;
import com.example.datn.repository.DotGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DotGiamGiaService {
    @Autowired
    private DotGiamGiaRepository repo;


    public Page<DotGiamGia> findAll(int page, int size, String sortBy) {
        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return repo.findAll(pageable);
    }

    public Optional<DotGiamGia> findById(UUID id) {
        return repo.findById(id);
    }

    public DotGiamGia save(DotGiamGia obj) {
        if (obj.getNgayKetThuc().before(obj.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        else {
            if (obj.getMa() == null || obj.getMa().isEmpty()) {
                obj.setMa(autoTaoMa());
            }
            return repo.save(obj);
        }
    }

    public void deleteById(UUID id) {
        repo.deleteById(id);
    }


    private String autoTaoMa() {
        long count = repo.count();
        return String.format("DGG%03d", count);
    }
}
