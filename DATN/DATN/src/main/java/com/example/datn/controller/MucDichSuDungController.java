package com.example.datn.controller;

import com.example.datn.entity.MucDichSuDung;
import com.example.datn.service.MucDichSuDungService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/admin/muc-dich-su-dung")
public class MucDichSuDungController {

    private final MucDichSuDungService service;

    public MucDichSuDungController(MucDichSuDungService service) {
        this.service = service;
    }

    // ----------------------------------------------------
    // 1. GET ALL (200 OK)
    // ----------------------------------------------------
    @GetMapping
    public List<MucDichSuDung> getAll() {
        return service.getAll();
    }

    // ----------------------------------------------------
    // 2. GET BY ID (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<MucDichSuDung> getById(@PathVariable UUID id) {
        // MucDichSuDungService đã được refactor để trả về Optional
        Optional<MucDichSuDung> mucDichSuDung = service.getById(id);

        return mucDichSuDung
                .map(mdsd -> ResponseEntity.ok(mdsd)) // Nếu tìm thấy, trả về 200 OK
                .orElseGet(() -> ResponseEntity.notFound().build()); // Nếu không, trả về 404 NOT FOUND
    }

    // ----------------------------------------------------
    // 3. CREATE (201 CREATED)
    // ----------------------------------------------------
    @PostMapping
    public ResponseEntity<MucDichSuDung> create( @RequestBody MucDichSuDung mdsd) { // THÊM @Valid
        MucDichSuDung createdMdsd = service.create(mdsd);
        // Trả về 201 Created là chuẩn RESTful
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMdsd);
    }

    // ----------------------------------------------------
    // 4. UPDATE (200 OK hoặc 404 NOT FOUND)
    // ----------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<MucDichSuDung> update(@PathVariable UUID id, @Valid @RequestBody MucDichSuDung mdsd) {
        try {
            MucDichSuDung updatedMdsd = service.update(id, mdsd);
            return ResponseEntity.ok(updatedMdsd); // 200 OK nếu cập nhật thành công
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
        // Gọi service.delete(id).
        service.delete(id);

        // Trả về 204 No Content là chuẩn RESTful khi xóa thành công
        return ResponseEntity.noContent().build();
    }
}