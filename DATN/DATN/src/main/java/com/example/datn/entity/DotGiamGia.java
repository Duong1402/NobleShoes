package com.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "dot_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotGiamGia {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 50, unique = true, nullable = false)
    @NotBlank(message = "Mã đợt giảm giá không được để trống")
    @Size(max = 50, message = "Mã đợt giảm giá không được vượt quá 50 ký tự")
    private String ma;

    @Column(length = 200, nullable = false)
    @NotBlank(message = "Tên đợt giảm giá không được để trống")
    @Size(max = 200, message = "Tên đợt giảm giá không được vượt quá 200 ký tự")
    private String ten;

    @Min(value = 0, message = "Giá trị giảm không được âm")
    @Max(value = 100, message = "Giá trị giảm tối đa là 100%")
    private Integer giaTriGiam;

    @DecimalMin(value = "0.0", inclusive = true, message = "Số tiền giảm tối đa phải lớn hơn hoặc bằng 0")
    private Double soTienGiamToiDa;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate ngayKetThuc;

    @NotNull(message = "Trạng thái không được để trống")
    private Boolean trangThai;
}
