package com.example.datn.controller;

import com.example.datn.entity.DiaChi;
import com.example.datn.service.DiaChiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin/dia-chi")
public class DiaChiController {

    @Autowired
    private DiaChiService diaChiService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DiaChi diaChi) { // Đổi kiểu trả về thành ResponseEntity<?>
        if (diaChi == null || diaChi.getKhachHang() == null || diaChi.getKhachHang().getId() == null) {
            return ResponseEntity.badRequest().body("Thông tin Khách Hàng hoặc Payload không hợp lệ.");
        }
        try {
            DiaChi created = diaChiService.createDiaChi(diaChi);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            // Xử lý lỗi từ Service (ví dụ: Địa chỉ phải liên kết với Khách Hàng)
            System.err.println("❌ LỖI CREATE ĐỊA CHỈ: " + e.getMessage());
            e.printStackTrace();

            String errorMessage = e.getMessage().contains("phải được liên kết")
                    ? e.getMessage()
                    : "Lỗi tạo mới dữ liệu. Vui lòng kiểm tra dữ liệu đầu vào.";

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    }

    @GetMapping
    public ResponseEntity<List<DiaChi>> getAll() {
        List<DiaChi> danhSachDiaChi = diaChiService.findAll();
        return new ResponseEntity<>(danhSachDiaChi, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaChi> getById(@PathVariable("id") UUID id) {
        Optional<DiaChi> diaChi = diaChiService.findById(id);
        return diaChi.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/khach-hang/{idKhachHang}")
    public ResponseEntity<List<DiaChi>> getByKhachHangId(@PathVariable("idKhachHang") UUID idKhachHang) {
        List<DiaChi> danhSachDiaChi = diaChiService.findAllByKhachHangId(idKhachHang);
        return new ResponseEntity<>(danhSachDiaChi, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @RequestBody DiaChi diaChi) {
        if (diaChi == null) {
            return ResponseEntity.badRequest().body("Payload địa chỉ không được rỗng.");
        }
        try {
            DiaChi updated = diaChiService.updateDiaChi(id, diaChi);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Log lỗi chi tiết để tìm nguyên nhân
            System.err.println("❌ LỖI UPDATE ĐỊA CHỈ: " + e.getMessage());
            e.printStackTrace(); // In stack trace để xem lỗi gốc (ví dụ: SQL Integrity/Truncation)

            String errorMessage = e.getMessage().contains("không tồn tại")
                    ? e.getMessage()
                    : "Lỗi cập nhật dữ liệu. Vui lòng kiểm tra độ dài/tính duy nhất của dữ liệu.";

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") UUID id) {
        try {
            diaChiService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}