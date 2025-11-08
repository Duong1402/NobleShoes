package com.example.datn.service;

import com.example.datn.entity.MucDichSuDung;
import com.example.datn.repository.MucDichSuDungRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MucDichSuDungService {

    private final MucDichSuDungRepository repository;

    public MucDichSuDungService(MucDichSuDungRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------------------
    // 1. CREATE: Thêm logic tự động sinh mã 'ma' và đồng bộ hóa
    // ----------------------------------------------------

    /**
     * Thêm mới Mục Đích Sử Dụng, tự động sinh mã và đảm bảo đồng bộ hóa
     * bằng 'synchronized'.
     */
    public synchronized MucDichSuDung create(MucDichSuDung newMd) {
        // Tự động sinh Mã (ma) theo format MDSD01, MDSD02, ...
        String newMa = generateNewMa();
        newMd.setMa(newMa);

        return repository.save(newMd);
    }

    /**
     * Hàm tự động sinh mã mục đích sử dụng theo format MDSD01, MDSD02, ...
     * Yêu cầu phương thức findTopByOrderByMaDesc() đã có trong MucDichSuDungRepository.
     */
    private String generateNewMa() {
        // 1. Tìm mã lớn nhất hiện tại trong DB (ví dụ: "MDSD09")
        String lastMa = repository.findTopByOrderByMaDesc()
                .map(MucDichSuDung::getMa)
                // Bắt đầu từ "MDSD00" để dễ dàng parse
                .orElse("MDSD00");

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

        return "MDSD" + formattedNum;
    }


    // ----------------------------------------------------
    // 2. UPDATE: Giữ nguyên mã 'ma' và chỉ cập nhật 'ten'
    // ----------------------------------------------------
    public MucDichSuDung update(UUID id, MucDichSuDung mdDetails) {
        MucDichSuDung existingMd = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mục đích sử dụng không tồn tại với id: " + id));

        // CẬP NHẬT TRƯỜNG TEN: Lấy tên mới từ request body
        existingMd.setTen(mdDetails.getTen());

        // TRƯỜNG 'MA' (Mã): GIỮ NGUYÊN giá trị cũ.

        return repository.save(existingMd);
    }


    // ----------------------------------------------------
    // 3. Các phương thức CRUD cơ bản
    // ----------------------------------------------------
    public List<MucDichSuDung> getAll() {
        return repository.findAll();
    }

    /**
     * Trả về Optional để tầng Controller xử lý việc không tìm thấy tài nguyên (404).
     */
    public Optional<MucDichSuDung> getById(UUID id) {
        return repository.findById(id);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}