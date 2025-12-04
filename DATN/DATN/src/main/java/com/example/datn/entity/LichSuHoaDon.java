package com.example.datn.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lich_su_hoa_don")
public class LichSuHoaDon {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", referencedColumnName = "id")
    @JsonBackReference
    private HoaDon hoaDon;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @Column(name = "nguoi_thuc_hien", length = 100)
    private String nguoiThucHien;

    @Column(name = "ghi_chu", length = 100)
    private String ghiChu;

    @Column(name = "trang_thai_moi")
    private Integer trangThaiMoi;
}
