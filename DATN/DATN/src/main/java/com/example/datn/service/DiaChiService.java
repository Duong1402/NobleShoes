package com.example.datn.service;

import com.example.datn.entity.DiaChi;
import com.example.datn.entity.KhachHang;
import com.example.datn.repository.DiaChiRepository;
import com.example.datn.repository.KhachHangRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DiaChiService {

    @Autowired
    private DiaChiRepository diaChiRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    public String generateNewMa() {
        // Gọi phương thức Repository có Lock
        String maxMa = diaChiRepository.findMaxMa();

        // Logic tăng mã:
        if (maxMa == null) return "DC01";

        try {
            int num = Integer.parseInt(maxMa.substring(2)) + 1;
            return String.format("DC%02d", num); // DC01, DC02, ...
        } catch (NumberFormatException e) {
            // Xử lý lỗi nếu mã không theo format mong muốn
            return "DC" + (System.currentTimeMillis() % 10000); // Fallback tạm thời
        }
    }

    @Transactional
    public DiaChi createDiaChi(DiaChi diaChi) {
        // Kiểm tra xem ID Địa Chỉ đã tồn tại chưa
        if (diaChi.getId() != null) {
            throw new IllegalArgumentException("Không thể tạo mới địa chỉ với ID đã tồn tại.");
        }

        // 1. Logic tạo Mã (Mã chỉ được tạo khi thêm mới)
        if (diaChi.getMa() == null || diaChi.getMa().trim().isEmpty()) {
            diaChi.setMa(generateNewMa()); // Giả định hàm này tồn tại
        }

        if (diaChi.getMacDinh() == null || diaChi.getMacDinh()) {
            // Nếu là địa chỉ mặc định (hoặc là địa chỉ đầu tiên)
            // Cập nhật tất cả các địa chỉ cũ khác của KH này thành FALSE
            diaChiRepository.updateAllDefaultsToFalse(diaChi.getKhachHang().getId(), null);
            diaChi.setMacDinh(true);
        }

        // 2. Xử lý mối quan hệ Khách Hàng (BẮT BUỘC)
        if (diaChi.getKhachHang() != null && diaChi.getKhachHang().getId() != null) {
            UUID khachHangId = diaChi.getKhachHang().getId();

            // Dùng getReferenceById để tạo Managed Entity Proxy, tránh lỗi Transient
            KhachHang managedKhachHang = khachHangRepository.getReferenceById(khachHangId);
            diaChi.setKhachHang(managedKhachHang);
        } else {
            throw new RuntimeException("Địa chỉ phải được liên kết với một Khách Hàng.");
        }

        // 3. Lưu bản ghi mới
        return diaChiRepository.save(diaChi);
    }

    @Transactional
    public DiaChi updateDiaChi(UUID diaChiId, DiaChi diaChi) {
        // 1. Tải bản ghi gốc từ DB
        DiaChi existingDiaChi = diaChiRepository.findById(diaChiId)
                .orElseThrow(() -> new RuntimeException("Địa chỉ (ID: " + diaChiId + ") không tồn tại để cập nhật."));

        // Kiểm tra và xử lý Mặc Định
        if (diaChi.getMacDinh() != null && diaChi.getMacDinh()) {
            // Nếu địa chỉ này được set là mặc định, hủy mặc định các địa chỉ khác
            // Tạo hàm này trong DiaChiRepository.java:
            // @Modifying @Query("UPDATE DiaChi dc SET dc.macDinh = FALSE WHERE dc.khachHang.id = :khId AND dc.id != :dcId")
            // void updateAllDefaultsToFalse(@Param("khId") UUID khachHangId, @Param("dcId") UUID diaChiId);

            diaChiRepository.updateAllDefaultsToFalse(existingDiaChi.getKhachHang().getId(), diaChiId);
            existingDiaChi.setMacDinh(true);
        } else {
            // Nếu Frontend gửi macDinh=false, chỉ cập nhật trường đó
            existingDiaChi.setMacDinh(false);
        }

        // 2. MERGE các trường: Chỉ cập nhật những trường được phép sửa
        // Cập nhật các trường địa chỉ chi tiết:
        existingDiaChi.setDiaChiCuThe(diaChi.getDiaChiCuThe());
        existingDiaChi.setXa(diaChi.getXa());
        existingDiaChi.setHuyen(diaChi.getHuyen());
        existingDiaChi.setThanhPho(diaChi.getThanhPho());

        // Cập nhật Mã nếu được gửi lên (thường không đổi khi update)
        if (diaChi.getMa() != null && !diaChi.getMa().trim().isEmpty()) {
            existingDiaChi.setMa(diaChi.getMa());
        }

        // **KHÔNG GHI ĐÈ mối quan hệ Khách Hàng (existingDiaChi.getKhachHang())**

        // 3. Lưu bản ghi đã merge
        return diaChiRepository.save(existingDiaChi);
    }

    public List<DiaChi> findAll() {
        return diaChiRepository.findAll();
    }

    public Optional<DiaChi> findById(UUID id) {
        return diaChiRepository.findById(id);
    }

    public List<DiaChi> findAllByKhachHangId(UUID idKhachHang) {
        return diaChiRepository.findAllByKhachHangId(idKhachHang);
    }

    public void deleteById(UUID id) {
        diaChiRepository.deleteById(id);
    }
}
