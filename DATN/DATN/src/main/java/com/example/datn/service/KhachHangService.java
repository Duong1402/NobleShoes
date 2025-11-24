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
    private final DiaChiService diaChiService;

    public KhachHangService(KhachHangRepository repo, DiaChiService diaChiService) {
        this.repo = repo;
        this.diaChiService = diaChiService;
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
        boolean isNew = obj.getId() == null;

        if (isNew && (obj.getMa() == null || obj.getMa().trim().isEmpty())) {
            obj.setMa(generateNewMa());
        }

        KhachHang savedKhachHang = repo.save(obj);
        return savedKhachHang;
    }

    public void deleteById(UUID id) {
        repo.deleteById(id);
    }

    public String generateNewMa() {
        String maxMa = repo.findMaxMaKhachHang();
        int nextNumber = 1;

        if (maxMa != null && maxMa.startsWith("KH")) {
            try {
                // Lấy phần số sau "KH"
                String numberPart = maxMa.substring(2);
                // Chuyển thành số và tăng lên 1
                nextNumber = Integer.parseInt(numberPart) + 1;
            } catch (NumberFormatException e) {
                // Xử lý nếu mã không đúng định dạng (ví dụ: KHabc)
                System.err.println("Lỗi parse mã Khách Hàng: " + maxMa);
                nextNumber = 1; // Khởi tạo lại từ KH01 nếu lỗi
            }
        }
        // Format lại thành chuỗi có 2 chữ số (KH01, KH02, KH10, KH100)
        return String.format("KH%02d", nextNumber);
    }
}