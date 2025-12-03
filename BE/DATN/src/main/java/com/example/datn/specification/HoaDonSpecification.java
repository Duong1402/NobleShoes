package com.example.datn.specification;

import com.example.datn.entity.HoaDon;
import com.example.datn.entity.NhanVien;
import com.example.datn.model.request.HoaDonFilterRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class HoaDonSpecification {

    public static Specification<HoaDon> filter(HoaDonFilterRequest filter) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (StringUtils.hasText(filter.getMa())) { // Check null và rỗng
                predicates.add(cb.like(root.get("ma"), "%" + filter.getMa().trim() + "%"));
            }

            if (StringUtils.hasText(filter.getTenKhachOrNhanVien())) {
                String searchTerm = "%" + filter.getTenKhachOrNhanVien().trim() + "%";


                Join<HoaDon, NhanVien> nhanVienJoin = root.join("nhanVien", JoinType.LEFT);


                Predicate tenKhachPredicate = cb.like(root.get("tenKhachHang"), searchTerm);
                Predicate tenNhanVienPredicate = cb.like(nhanVienJoin.get("hoTen"), searchTerm);


                predicates.add(cb.or(tenKhachPredicate, tenNhanVienPredicate));
            }


            if (filter.getNgayTu() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayTao"), filter.getNgayTu()));
            }
            if (filter.getNgayDen() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayTao"), filter.getNgayDen()));
            }

            if (StringUtils.hasText(filter.getLoaiDon())) {
                predicates.add(cb.equal(root.get("loaiHoaDon"), filter.getLoaiDon()));
            }

            if (filter.getTrangThai() != null) {
                predicates.add(cb.equal(root.get("trangThai"), filter.getTrangThai()));
            }


            query.orderBy(cb.desc(root.get("ngayTao")));


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}