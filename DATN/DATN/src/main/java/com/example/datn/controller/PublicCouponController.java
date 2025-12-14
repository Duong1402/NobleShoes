package com.example.datn.controller;

import com.example.datn.entity.PhieuGiamGia;
import com.example.datn.model.Response.PublicCouponResponse;
import com.example.datn.repository.PhieuGiamGiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/phieu-giam-gia")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5175",
        "http://localhost:5176"
})
@RequiredArgsConstructor
public class PublicCouponController {

    private final PhieuGiamGiaRepository repo;

    private LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private PublicCouponResponse toDto(PhieuGiamGia p) {
        return new PublicCouponResponse(
                p.getId(),
                p.getMa(),
                p.getTen(),
                p.getTrangThai(),
                p.getHinhThucGiamGia(),
                p.getGiaTriGiam(),
                p.getGiaTriGiamToiDa(),
                p.getGiaTriGiamToiThieu(),
                toLocalDate(p.getNgayBatDau()),
                toLocalDate(p.getNgayKetThuc())
        );
    }

    // GET /api/public/phieu-giam-gia
    @GetMapping
    public ResponseEntity<List<PublicCouponResponse>> getAllPublic() {
        List<PublicCouponResponse> list = repo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // GET /api/public/phieu-giam-gia/{ma}
    @GetMapping("/{ma}")
    public ResponseEntity<?> getByMa(@PathVariable String ma) {
        String code = (ma == null) ? "" : ma.trim();
        if (code.isBlank()) return ResponseEntity.badRequest().body("Mã giảm giá không hợp lệ");

        return repo.findByMaIgnoreCase(code)
                .map(p -> ResponseEntity.ok(toDto(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
