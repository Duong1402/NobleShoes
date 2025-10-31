package com.example.datn.controller;

import com.example.datn.model.Response.HoaDonDetailResponse;
import com.example.datn.model.Response.HoaDonResponse;
import com.example.datn.model.request.HoaDonFilterRequest;
import com.example.datn.model.request.UpdateHoaDonRequest;
import com.example.datn.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/admin/hoa-don")
@CrossOrigin(origins = "http://localhost:5173")
public class HoaDonController {

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping
    public ResponseEntity<Page<HoaDonResponse>> searchHoaDon(
            HoaDonFilterRequest filter,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        Page<HoaDonResponse> result = hoaDonService.searchHoaDon(filter, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HoaDonDetailResponse> getHoaDonDetail(@PathVariable("id") UUID id) {
        HoaDonDetailResponse detail = hoaDonService.getHoaDonDetail(id);
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HoaDonResponse> updateHoaDon(
            @PathVariable("id") UUID id,
            @RequestBody UpdateHoaDonRequest request
    ) {
        HoaDonResponse updated = hoaDonService.updateHoaDon(id, request);
        return ResponseEntity.ok(updated);
    }
}