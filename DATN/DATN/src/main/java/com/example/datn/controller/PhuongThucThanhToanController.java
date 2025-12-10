package com.example.datn.controller;

import com.example.datn.entity.PhuongThucThanhToan;
import com.example.datn.service.PhuongThucThanhToanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "http://localhost:*")
public class PhuongThucThanhToanController {

    private final PhuongThucThanhToanService service;

    /* ===================== ALL (ADMIN + CLIENT) ===================== */

    // GET /admin/phuong-thuc-thanh-toan  OR  /api/phuong-thuc-thanh-toan
    @GetMapping({"/admin/phuong-thuc-thanh-toan", "/api/phuong-thuc-thanh-toan"})
    public ResponseEntity<List<PhuongThucThanhToan>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // GET /admin/phuong-thuc-thanh-toan/{id}  OR  /api/phuong-thuc-thanh-toan/{id}
    @GetMapping({"/admin/phuong-thuc-thanh-toan/{id}", "/api/phuong-thuc-thanh-toan/{id}"})
    public ResponseEntity<PhuongThucThanhToan> getById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /admin/phuong-thuc-thanh-toan  OR  /api/phuong-thuc-thanh-toan
    @PostMapping({"/admin/phuong-thuc-thanh-toan", "/api/phuong-thuc-thanh-toan"})
    public ResponseEntity<PhuongThucThanhToan> create(@RequestBody PhuongThucThanhToan request) {
        return ResponseEntity.ok(service.save(request));
    }

    // PUT /admin/phuong-thuc-thanh-toan/{id}  OR  /api/phuong-thuc-thanh-toan/{id}
    @PutMapping({"/admin/phuong-thuc-thanh-toan/{id}", "/api/phuong-thuc-thanh-toan/{id}"})
    public ResponseEntity<PhuongThucThanhToan> update(
            @PathVariable UUID id,
            @RequestBody PhuongThucThanhToan request
    ) {
        request.setId(id);
        return ResponseEntity.ok(service.save(request));
    }

    // DELETE /admin/phuong-thuc-thanh-toan/{id}  OR  /api/phuong-thuc-thanh-toan/{id}
    @DeleteMapping({"/admin/phuong-thuc-thanh-toan/{id}", "/api/phuong-thuc-thanh-toan/{id}"})
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // hỗ trợ cả 2 kiểu service (delete hoặc deleteById)
        try {
            service.deleteById(id);
        } catch (NoSuchMethodError | Exception ignore) {
            // fallback nếu service bạn tên là delete(UUID)
            try { service.getClass().getMethod("delete", UUID.class).invoke(service, id); }
            catch (Exception e) { /* nếu vẫn lỗi thì để Spring trả 500 */ throw new RuntimeException(e); }
        }
        return ResponseEntity.noContent().build();
    }
}
