package com.example.datn.controller;

import com.example.datn.dto.SanPhamPublicDTO;
import com.example.datn.entity.SanPham;
import com.example.datn.repository.SanPhamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicProductController {

    private final SanPhamRepository sanPhamRepository;

    // ✅ FE đang gọi: /api/public/san-pham/all
    @GetMapping("/san-pham/all")
    public List<SanPhamPublicDTO> getAllPublic() {
        List<SanPham> list = sanPhamRepository.findAll();
        List<SanPhamPublicDTO> out = new ArrayList<>();

        for (SanPham sp : list) {
            UUID id = readUUID(sp, "getId");
            String ten = firstNonBlank(
                    readString(sp, "getTen"),
                    readString(sp, "getTenSanPham"),
                    readString(sp, "getName"),
                    readString(sp, "getMa")
            );

            BigDecimal giaMin = pickMinPrice(sp);
            String img = pickMainImage(sp);

            out.add(new SanPhamPublicDTO(id, ten, giaMin, img));
        }
        return out;
    }

    // (Tuỳ bạn dùng) /api/public/san-pham/{id}
    @GetMapping("/san-pham/{id}")
    public SanPhamPublicDTO getOne(@PathVariable UUID id) {
        SanPham sp = sanPhamRepository.findById(id).orElseThrow();
        String ten = firstNonBlank(
                readString(sp, "getTen"),
                readString(sp, "getTenSanPham"),
                readString(sp, "getName"),
                readString(sp, "getMa")
        );
        return new SanPhamPublicDTO(id, ten, pickMinPrice(sp), pickMainImage(sp));
    }

    /* ================== Helpers ================== */

    // ✅ lấy list chi tiết bằng reflection để khỏi phụ thuộc tên field
    @SuppressWarnings("unchecked")
    private List<Object> readChiTietList(SanPham sp) {
        Object v = readObject(sp, "getChiTietSanPhams");
        if (v instanceof List) return (List<Object>) v;
        v = readObject(sp, "getChiTietSanPham");
        if (v instanceof List) return (List<Object>) v;
        return Collections.emptyList();
    }

    private BigDecimal pickMinPrice(SanPham sp) {
        List<Object> cts = readChiTietList(sp);
        if (cts.isEmpty()) return BigDecimal.ZERO;

        BigDecimal min = null;
        for (Object ct : cts) {
            BigDecimal gia = readBigDecimal(ct, "getGiaBan");
            if (gia == null) gia = readBigDecimal(ct, "getGia");
            if (gia == null) gia = BigDecimal.ZERO;

            if (min == null || gia.compareTo(min) < 0) min = gia;
        }
        return min == null ? BigDecimal.ZERO : min;
    }

    private String pickMainImage(SanPham sp) {
        // ✅ thử nhiều getter phổ biến trên SanPham
        String img = firstNonBlank(
                readString(sp, "getHinhAnhUrl"),
                readString(sp, "getHinhAnh"),
                readString(sp, "getAnh"),
                readString(sp, "getAnhDaiDien"),
                readString(sp, "getThumbnail"),
                readString(sp, "getImage"),
                readString(sp, "getUrlAnh"),
                readString(sp, "getUrl")
        );
        if (isNonBlank(img)) return img;

        // ✅ fallback từ ChiTietSanPham đầu tiên
        List<Object> cts = readChiTietList(sp);
        if (!cts.isEmpty()) {
            Object ct = cts.get(0);
            img = firstNonBlank(
                    readString(ct, "getHinhAnhUrl"),
                    readString(ct, "getHinhAnh"),
                    readString(ct, "getUrlAnh1"),
                    readString(ct, "getUrlAnh2"),
                    readString(ct, "getUrlAnh3"),
                    readString(ct, "getAnh"),
                    readString(ct, "getThumbnail"),
                    readString(ct, "getUrl")
            );
            if (isNonBlank(img)) return img;
        }
        return null;
    }

    private boolean isNonBlank(String s) { return s != null && !s.trim().isEmpty(); }

    private String firstNonBlank(String... arr) {
        for (String s : arr) if (isNonBlank(s)) return s.trim();
        return null;
    }

    private Object readObject(Object obj, String getter) {
        try {
            Method m = obj.getClass().getMethod(getter);
            return m.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private String readString(Object obj, String getter) {
        Object v = readObject(obj, getter);
        return v == null ? null : String.valueOf(v);
    }

    private UUID readUUID(Object obj, String getter) {
        Object v = readObject(obj, getter);
        if (v instanceof UUID) return (UUID) v;
        try { return v == null ? null : UUID.fromString(v.toString()); } catch (Exception e) { return null; }
    }

    private BigDecimal readBigDecimal(Object obj, String getter) {
        Object v = readObject(obj, getter);
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        try { return v == null ? null : new BigDecimal(v.toString()); } catch (Exception e) { return null; }
    }
}
