package com.example.datn.controller;

import com.example.datn.entity.ThuongHieu;
import com.example.datn.service.ThuongHieuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/thuong-hieu")
public class ThuongHieuController {

    private final ThuongHieuService service;

    public ThuongHieuController(ThuongHieuService service) {
        this.service = service;
    }

    // ----------------------------------------------------
    // 1. GET ALL (200 OK)
    // ----------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ThuongHieu>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ----------------------------------------------------
    // 2. GET BY ID (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieu> getById(@PathVariable UUID id) {
        // Sử dụng service đã được refactor để trả về Optional
        return service.getById(id)
                .map(ResponseEntity::ok) // Nếu tìm thấy, trả về 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Nếu không, trả về 404 NOT FOUND
    }

    // ----------------------------------------------------
    // 3. CREATE (201 CREATED)
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<ThuongHieu> create(@RequestBody ThuongHieu th) { // Thêm @Valid
        ThuongHieu created = service.create(th);
        // Trả về 201 Created là chuẩn RESTful cho thao tác tạo mới thành công
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ----------------------------------------------------
    // 4. UPDATE (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ThuongHieu> update(@PathVariable UUID id, @Valid @RequestBody ThuongHieu th) {
        try {
            ThuongHieu updated = service.update(id, th);
            return ResponseEntity.ok(updated); // 200 OK nếu cập nhật thành công
        } catch (RuntimeException e) {
            // Bắt lỗi nếu Service ném ra Exception (khi không tìm thấy ID)
            return ResponseEntity.notFound().build(); // 404 NOT FOUND
        }
    }

    // ----------------------------------------------------
    // 5. DELETE (204 NO CONTENT)
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Gọi service.delete(id).
        service.delete(id);

        // Trả về 204 No Content là chuẩn RESTful khi xóa thành công
        return ResponseEntity.noContent().build();
    }
}