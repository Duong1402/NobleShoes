package com.example.datn.controller;

import com.example.datn.entity.DeGiay;
import com.example.datn.service.DeGiayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin/de-giay")
public class DeGiayController {

    private final DeGiayService service;

    public DeGiayController(DeGiayService service) {
        this.service = service;
    }

    // ----------------------------------------------------
    // 1. GET ALL (200 OK)
    // ----------------------------------------------------
    @GetMapping
    public List<DeGiay> getAll() {
        return service.getAll();
    }

    // ----------------------------------------------------
    // 2. GET BY ID (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DeGiay> getById(@PathVariable UUID id) {
        // DeGiayService đã được refactor để trả về Optional
        Optional<DeGiay> deGiay = service.getById(id);

        return deGiay
                .map(dg -> ResponseEntity.ok(dg)) // Nếu tìm thấy, trả về 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Nếu không, trả về 404 NOT FOUND
    }

    // ----------------------------------------------------
    // 3. CREATE (201 CREATED)
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<DeGiay> create(@RequestBody DeGiay dg) { // THÊM @Valid
        DeGiay createdDg = service.create(dg);
        // Trả về 201 Created là chuẩn RESTful
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDg);
    }

    // ----------------------------------------------------
    // 4. UPDATE (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<DeGiay> update(@PathVariable UUID id, @Valid @RequestBody DeGiay dg) {
        try {
            DeGiay updatedDg = service.update(id, dg);
            return ResponseEntity.ok(updatedDg); // 200 OK nếu cập nhật thành công
        } catch (RuntimeException e) {
            // Xử lý lỗi nếu Service ném ra Exception (vì không tìm thấy ID)
            return ResponseEntity.notFound().build(); // 404 NOT FOUND
        }
    }

    // ----------------------------------------------------
    // 5. DELETE (204 NO CONTENT)
    // ----------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        // Gọi service.delete(id). Thường trả về 204 bất kể bản ghi có tồn tại hay không.
        service.delete(id);

        // Trả về 204 No Content là chuẩn RESTful khi xóa thành công
        return ResponseEntity.noContent().build();
    }
}