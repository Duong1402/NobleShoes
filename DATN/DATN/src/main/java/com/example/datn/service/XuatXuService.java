package com.example.datn.service;

import com.example.datn.entity.XuatXu;
import com.example.datn.repository.XuatXuRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class XuatXuService {

    private final XuatXuRepository repository; // Đổi tên biến thành 'repository' cho nhất quán

    public XuatXuService(XuatXuRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------------------
    // 1. CREATE: Thêm logic tự động sinh mã 'ma' và đồng bộ hóa
    // ----------------------------------------------------
    /**
     * Thêm mới Xuất Xứ, tự động sinh mã và đảm bảo đồng bộ hóa
     * bằng 'synchronized' để tránh Race Condition khi sinh mã.
     */
    public synchronized XuatXu create(XuatXu newXx) {
        // Tự động sinh Mã (ma) theo format XX01, XX02, ...
        String newMa = generateNewMa();
        newXx.setMa(newMa);

        return repository.save(newXx);
    }

    /**
     * Hàm tự động sinh mã Xuất Xứ theo format XX01, XX02, ...
     * Yêu cầu phương thức findTopByOrderByMaDesc() đã có trong XuatXuRepository.
     */
    private String generateNewMa() {
        // 1. Tìm mã lớn nhất hiện tại trong DB (ví dụ: "XX09")
        String lastMa = repository.findTopByOrderByMaDesc()
                .map(XuatXu::getMa)
                // Bắt đầu từ "XX00" để dễ dàng parse
                .orElse("XX00");

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

        return "XX" + formattedNum;
    }


    // ----------------------------------------------------
    // 2. UPDATE: Giữ nguyên mã 'ma' và chỉ cập nhật 'ten'
    // ----------------------------------------------------
    public XuatXu update(UUID id, XuatXu xxDetails) {
        XuatXu existingXx = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Xuất Xứ không tồn tại với id: " + id));

        // CẬP NHẬT TRƯỜNG TEN: Lấy tên mới từ request body
        existingXx.setTen(xxDetails.getTen());

        // TRƯỜNG 'MA' (Mã): GIỮ NGUYÊN giá trị cũ của existingXx.

        return repository.save(existingXx);
    }


    // ----------------------------------------------------
    // 3. Các phương thức CRUD cơ bản
    // ----------------------------------------------------
    public List<XuatXu> getAll() {
        return repository.findAll();
    }

    /**
     * Trả về Optional để tầng Controller xử lý việc không tìm thấy tài nguyên (404).
     */
    public Optional<XuatXu> getById(UUID id) {
        return repository.findById(id);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}