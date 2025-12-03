package com.example.datn.service;

import com.example.datn.entity.DanhMuc;
import com.example.datn.repository.DanhMucRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DanhMucService {

    private final DanhMucRepository repository;

    public DanhMucService(DanhMucRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------------------
    // 1. CREATE: Thêm logic tự động sinh mã 'ma'
    // ----------------------------------------------------
    public DanhMuc create(DanhMuc newDm) {
        // Tự động sinh Mã (ma) theo format DM09, DM10, ...
        String newMa = generateNewMa();
        newDm.setMa(newMa);

        // Giả sử logic validation cho 'ten' đã được xử lý ở đây hoặc ở Entity (với Validation Group)

        // Lưu vào DB. DB/JPA sẽ tự động sinh id (UUID)
        return repository.save(newDm);
    }

    /**
     * Hàm tự động sinh mã danh mục theo format DM01, DM02, ..., DM09, DM10, ...
     * Truy vấn DB để lấy mã lớn nhất hiện tại.
     */
    private String generateNewMa() {
        // 1. Tìm mã lớn nhất hiện tại trong DB (ví dụ: "DM09")
        // Yêu cầu phương thức findTopByOrderByMaDesc() đã có trong DanhMucRepository
        String lastMa = repository.findTopByOrderByMaDesc()
                .map(DanhMuc::getMa)
                // Nếu chưa có mã nào, bắt đầu từ "DM00" để dễ dàng parse
                .orElse("DM00");

        // 2. Trích xuất phần số từ mã lớn nhất
        // lastMa = "DM09" => numPart = "09"
        String numPart = lastMa.replaceAll("[^0-9]", "");

        int currentNum;
        try {
            currentNum = Integer.parseInt(numPart);
        } catch (NumberFormatException e) {
            // Xử lý trường hợp mã không hợp lệ, hoặc không tìm thấy số
            currentNum = 0;
        }

        // 3. Tăng lên 1
        int nextNum = currentNum + 1;

        // 4. Định dạng lại chuỗi (luôn có 2 chữ số: 01-99)
        // Dùng "%02d" để đảm bảo số 1-9 có thêm số 0 ở đầu (01, ..., 09), 10 trở đi là 10, 11...
        String formattedNum = String.format("%02d", nextNum);

        return "DM" + formattedNum;
    }


    // ----------------------------------------------------
    // 2. UPDATE: Giữ nguyên mã 'ma' và chỉ cập nhật 'ten'
    // ----------------------------------------------------
    public DanhMuc update(UUID id, DanhMuc dmDetails) { // Giữ nguyên ID là UUID
        DanhMuc existingDm = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại với id: " + id));

        // CẬP NHẬT TRƯỜNG TEN: Lấy tên mới từ request body
        existingDm.setTen(dmDetails.getTen());

        // TRƯỜNG 'MA' (Mã danh mục): GIỮ NGUYÊN giá trị cũ của existingDm.
        // Dữ liệu 'ma' gửi lên từ client (dmDetails) sẽ bị BỎ QUA.

        return repository.save(existingDm);
    }


    // ----------------------------------------------------
    // 3. Các phương thức CRUD cơ bản
    // ----------------------------------------------------
    public List<DanhMuc> getAll() {
        // Nếu cần sắp xếp mới nhất lên đầu (vì ID là UUID), nên sắp xếp ở đây hoặc trong Repository
        // Giả sử bạn sắp xếp bằng cách thêm ORDER BY vào truy vấn SQL của Repository:
        // return repository.findAllByOrderByNgayTaoDesc(); 
        // HOẶC trả về mặc định để frontend sắp xếp:
        return repository.findAll();
    }

    public DanhMuc getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}