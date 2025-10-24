package com.example.datn.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ChiTietSanPhamRequest {

    @NotBlank(message = "Tên biến thể không được để trống")
    @Size(max = 300, message = "Tên biến thể không được vượt quá 300 ký tự")
    private String ten;

    @NotNull(message = "Màu sắc không được để trống")
    private UUID idMauSac;

    @NotNull(message = "Kích thước không được để trống")
    private UUID idKichThuoc;

    @NotNull(message = "Chất liệu không được để trống")
    private UUID idChatLieu;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    @Digits(integer = 12, fraction = 2, message = "Giá bán không hợp lệ (tối đa 12 số nguyên và 2 số thập phân)")
    private BigDecimal giaBan;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được nhỏ hơn 0")
    private Integer soLuongTon;

    @Size(max = 300, message = "Mô tả không được vượt quá 300 ký tự")
    private String moTa;

    @Size(max = 3, message = "Tối đa 3 ảnh cho sản phẩm")
    private List<String> images; // URL ảnh Cloudinary

}
