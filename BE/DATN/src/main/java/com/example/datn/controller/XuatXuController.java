package com.example.datn.controller;

import com.example.datn.entity.XuatXu;
import com.example.datn.service.XuatXuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/xuat-xu")
public class XuatXuController {

    private final XuatXuService service;

    public XuatXuController(XuatXuService service) {
        this.service = service;
    }

    // ----------------------------------------------------
    // 1. GET ALL (200 OK)
    // ----------------------------------------------------
    @GetMapping
    public ResponseEntity<List<XuatXu>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ----------------------------------------------------
    // 2. GET BY ID (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<XuatXu> getById(@PathVariable UUID id) {
        // Sử dụng service đã được refactor để trả về Optional
        return service.getById(id)
                .map(ResponseEntity::ok) // Nếu tìm thấy, trả về 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Nếu không, trả về 404 NOT FOUND
    }

    // ----------------------------------------------------
    // 3. CREATE (201 CREATED)
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<XuatXu> create(@RequestBody XuatXu xx) { // THÊM @Valid
        XuatXu created = service.create(xx);
        // Trả về 201 Created là chuẩn RESTful cho thao tác tạo mới thành công
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ----------------------------------------------------
    // 4. UPDATE (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<XuatXu> update(@PathVariable UUID id, @Valid @RequestBody XuatXu xx) {
        try {
            XuatXu updated = service.update(id, xx);
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