package com.example.datn.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SanPhamRequest {

    private UUID id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 300, message = "Tên sản phẩm không được vượt quá 300 ký tự")
    private String ten;

    @Size(max = 50, message = "Mã sản phẩm không được vượt quá 50 ký tự")
    private String ma;

    @NotNull(message = "Danh mục không được để trống")
    private UUID idDanhMuc;

    @NotNull(message = "Thương hiệu không được để trống")
    private UUID idThuongHieu;

    @NotNull(message = "Mục đích sử dụng không được để trống")
    private UUID idMucDichSuDung;

    @NotNull(message = "Đế giày không được để trống")
    private UUID idDeGiay;

    @NotNull(message = "Dây giày không được để trống")
    private UUID idDayGiay;

    @NotNull(message = "Xuất xứ không được để trống")
    private UUID idXuatXu;


    @NotEmpty(message = "Danh sách chi tiết sản phẩm không được để trống")
    @Valid
    private List<ChiTietSanPhamRequest> chiTietSanPham;
}
