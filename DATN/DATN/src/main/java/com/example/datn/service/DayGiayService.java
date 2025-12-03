package com.example.datn.service;

import com.example.datn.entity.DayGiay;
import com.example.datn.repository.DayGiayRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DayGiayService {

    private final DayGiayRepository repository; // Đổi tên biến thành 'repository' cho nhất quán

    public DayGiayService(DayGiayRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------------------
    // 1. CREATE: Thêm logic tự động sinh mã 'ma' và đồng bộ hóa
    // ----------------------------------------------------

    /**
     * Thêm mới Dây Giày, tự động sinh mã và đảm bảo đồng bộ hóa
     * bằng 'synchronized' để tránh Race Condition khi sinh mã.
     */
    public synchronized DayGiay create(DayGiay newDg) {
        // Tự động sinh Mã (ma) theo format DG01, DG02, ...
        String newMa = generateNewMa();
        newDg.setMa(newMa);

        return repository.save(newDg);
    }

    /**
     * Hàm tự động sinh mã Dây Giày theo format DG01, DG02, ...
     * Yêu cầu phương thức findTopByOrderByMaDesc() đã có trong DayGiayRepository.
     */
    private String generateNewMa() {
        // 1. Tìm mã lớn nhất hiện tại trong DB (ví dụ: "DG09")
        String lastMa = repository.findTopByOrderByMaDesc()
                .map(DayGiay::getMa)
                // Bắt đầu từ "DG00" để dễ dàng parse
                .orElse("DG00");

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

        return "DG" + formattedNum;
    }


    // ----------------------------------------------------
    // 2. UPDATE: Giữ nguyên mã 'ma' và chỉ cập nhật 'ten'
    // ----------------------------------------------------
    public DayGiay update(UUID id, DayGiay dgDetails) {
        DayGiay existingDg = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dây Giày không tồn tại với id: " + id));

        // CẬP NHẬT TRƯỜNG TEN: Lấy tên mới từ request body
        existingDg.setTen(dgDetails.getTen());

        // TRƯỜNG 'MA' (Mã): GIỮ NGUYÊN giá trị cũ của existingDg.

        return repository.save(existingDg);
    }


    // ----------------------------------------------------
    // 3. Các phương thức CRUD cơ bản
    // ----------------------------------------------------
    public List<DayGiay> getAll() {
        return repository.findAll();
    }

    /**
     * Trả về Optional để tầng Controller xử lý việc không tìm thấy tài nguyên (404).
     */
    public Optional<DayGiay> getById(UUID id) {
        return repository.findById(id);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}