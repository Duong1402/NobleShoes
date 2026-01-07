package com.example.datn.controller;

import com.example.datn.entity.ChiTietSanPham;
import com.example.datn.entity.SanPham;
import com.example.datn.repository.SanPhamRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final SanPhamRepository sanPhamRepository;
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,###");

    public ChatController(SanPhamRepository sanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, Object> body) {
        String userText = extractUserMessage(body).toLowerCase().trim();
        List<Map<String, Object>> results = new ArrayList<>();
        boolean found = false;

        // 1) Tìm theo ngân sách
        double budgetValue = extractBudget(userText);
        if (budgetValue > 0) {
            BigDecimal budget = BigDecimal.valueOf(budgetValue);
            List<SanPham> list = sanPhamRepository.findByChiTietSanPhamsGiaBanLessThanEqual(budget);
            if (!list.isEmpty()) {
                found = true;
                results.add(Map.of(
                        "type", "budget",
                        "products", formatProducts(list)
                ));
            }
        }

        // 2) Tìm kiếm đa trường
        if (!found) {
            List<SanPham> list = sanPhamRepository.searchByKeyword(userText);
            if (!list.isEmpty()) {
                found = true;
                results.add(Map.of(
                        "type", "search",
                        "products", formatProducts(list)
                ));
            }
        }

        // 3) Không tìm thấy -> gợi ý
        if (!found) {
            List<SanPham> randomList = sanPhamRepository.findTop5Random();
            results.add(Map.of(
                    "type", "none",
                    "products", formatProducts(randomList)
            ));
        }

        return ResponseEntity.ok(Map.of("results", results));
    }

    // -----------------------------
    // Lấy tin nhắn cuối cùng người dùng gửi
    private String extractUserMessage(Map<String, Object> body) {
        List<Map<String, Object>> messages = (List<Map<String, Object>>) body.get("messages");
        return messages.get(messages.size() - 1).get("content").toString();
    }

    // -----------------------------
    // Format sản phẩm đầy đủ thông tin
    private List<Map<String, Object>> formatProducts(List<SanPham> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SanPham sp : list) {
            for (ChiTietSanPham ct : sp.getChiTietSanPhams()) {
                Map<String, Object> p = new LinkedHashMap<>();
                p.put("ten", sp.getTen());
                if (sp.getHinhAnh() != null) {
                    p.put("hinhAnhUrl", sp.getHinhAnh().getUrlAnh1());
                } else {
                    p.put("hinhAnhUrl", "https://via.placeholder.com/150");
                }
                p.put("gia", formatPrice(ct.getGiaBan().doubleValue()));
                p.put("mauSac", ct.getMauSac().getTen());
                p.put("kichThuoc", ct.getKichThuoc().getTen());
                p.put("chatLieu", ct.getChatLieu().getTen());
                p.put("thuongHieu", sp.getThuongHieu() != null ? sp.getThuongHieu().getTen() : "Không có");
                p.put("danhMuc", sp.getDanhMuc().getTen());
                result.add(p);
            }
        }
        return result;
    }

    // -----------------------------
    // Format giá tiền
    private String formatPrice(double price) {
        return PRICE_FORMAT.format(price) + " VND";
    }

    // -----------------------------
    // Lấy số từ text
    private double extractBudget(String text) {
        try {
            Pattern p = Pattern.compile("(\\d+[\\.,]?\\d*)");
            Matcher m = p.matcher(text);
            if (m.find()) {
                double value = Double.parseDouble(m.group(1).replace(",", ""));
                if (text.contains("k")) value *= 1000;
                if (text.contains("triệu")) value *= 1_000_000;
                return value;
            }
        } catch (Exception ignored) {}
        return 0;
    }
}