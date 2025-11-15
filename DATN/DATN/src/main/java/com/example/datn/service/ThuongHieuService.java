package com.example.datn.service;

import com.example.datn.entity.ThuongHieu;
import com.example.datn.repository.ThuongHieuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class ThuongHieuService {

    private final ThuongHieuRepository repository; // Đổi tên biến thành 'repository' cho nhất quán

    public ThuongHieuService(ThuongHieuRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------------------
    // 1. CREATE: Thêm logic tự động sinh mã 'ma' và đồng bộ hóa
    // ----------------------------------------------------
    /**
     * Thêm mới Thương Hiệu, tự động sinh mã và đảm bảo đồng bộ hóa
     * bằng 'synchronized' để tránh Race Condition khi sinh mã.
     */
    public synchronized ThuongHieu create(ThuongHieu newTh) {
        // Tự động sinh Mã (ma) theo format TH01, TH02, ...
        String newMa = generateNewMa();
        newTh.setMa(newMa);

        return repository.save(newTh);
    }

    /**
     * Hàm tự động sinh mã Thương Hiệu theo format TH01, TH02, ...
     * Yêu cầu phương thức findTopByOrderByMaDesc() đã có trong ThuongHieuRepository.
     */
    private String generateNewMa() {
        // 1. Tìm mã lớn nhất hiện tại trong DB (ví dụ: "TH09")
        String lastMa = repository.findTopByOrderByMaDesc()
                .map(ThuongHieu::getMa)
                // Bắt đầu từ "TH00" để dễ dàng parse
                .orElse("TH00");

        // 2. Trích xuất phần số từ mã lớn nhất
        String numPart = lastMa.replaceAll("[^0-9]", "");

        int currentNum;
        try {
            currentNum = Integer.parseInt(numPart);
        } catch (NumberFormatException e) {
            currentNum = 0;
        }

        // 3. Tăng lên 1
        int nextNum = currentNum + 1;

        // 4. Định dạng lại chuỗi (luôn có 2 chữ số: 01-99)
        String formattedNum = String.format("%02d", nextNum);

        return "TH" + formattedNum;
    }


    // ----------------------------------------------------
    // 2. UPDATE: Giữ nguyên mã 'ma' và chỉ cập nhật 'ten'
    // ----------------------------------------------------
    public ThuongHieu update(UUID id, ThuongHieu thDetails) {
        ThuongHieu existingTh = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Thương Hiệu không tồn tại với id: " + id));

        // CẬP NHẬT TRƯỜNG TEN: Lấy tên mới từ request body
        existingTh.setTen(thDetails.getTen());

        // TRƯỜNG 'MA' (Mã): GIỮ NGUYÊN giá trị cũ của existingTh.

        return repository.save(existingTh);
    }


    // ----------------------------------------------------
    // 3. Các phương thức CRUD cơ bản
    // ----------------------------------------------------
    public List<ThuongHieu> getAll() {
        return repository.findAll();
    }

    /**
     * Trả về Optional để tầng Controller xử lý việc không tìm thấy tài nguyên (404).
     */
    public Optional<ThuongHieu> getById(UUID id) {
        return repository.findById(id);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}