package com.example.datn.controller;

import com.example.datn.dto.thongke.FilteredDataResponse;
import com.example.datn.dto.thongke.OverviewResponse;
import com.example.datn.dto.thongke.ThongKeFilterRequest;
import com.example.datn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/thong-ke")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ThongKeController {

    private final ThongKeService thongKeService;

    @GetMapping("/overview")
    public ResponseEntity<OverviewResponse> getOverviewData(@PageableDefault(page = 0, size = 5) Pageable lowStockPageable) {
        return ResponseEntity.ok(thongKeService.getOverviewData(lowStockPageable));
    }

    @GetMapping("/filtered")
    public ResponseEntity<FilteredDataResponse> getFilteredData(

            @ModelAttribute ThongKeFilterRequest filter,
            @PageableDefault(page = 0, size = 5) Pageable pageable
    ) {
        FilteredDataResponse response = thongKeService.getFilteredData(filter, pageable);
        return ResponseEntity.ok(response);
    }
}