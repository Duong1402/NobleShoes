package com.example.datn.service;

import com.example.datn.entity.*;
import com.example.datn.model.Response.HoaDonChiTietResponse;
import com.example.datn.model.Response.HoaDonDetailResponse;
import com.example.datn.model.Response.HoaDonResponse;
import com.example.datn.model.request.HoaDonFilterRequest;
import com.example.datn.model.request.UpdateHoaDonRequest;
import com.example.datn.repository.HoaDonChiTietRepository;
import com.example.datn.repository.HoaDonRepository;
import com.example.datn.repository.LichSuHoaDonRepository;
import com.example.datn.specification.HoaDonSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    public Page<HoaDonResponse> searchHoaDon(HoaDonFilterRequest filter, Pageable pageable) {
        Specification<HoaDon> spec = HoaDonSpecification.filter(filter);

        Page<HoaDon> hoaDonPage = hoaDonRepository.findAll(spec, pageable);


        return hoaDonPage.map(HoaDonResponse::new);
    }

    @Transactional(readOnly = true)
    public HoaDonDetailResponse getHoaDonDetail(UUID id) {

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (hoaDon.getKhachHang() != null) {
            List<DiaChi> lst = hoaDon.getKhachHang().getDanhSachDiaChi();

            if (lst != null) {
                lst.size();
            }
        }

        List<HoaDonChiTiet> chiTiets = hoaDonChiTietRepository.findAllByHoaDonId(id);


        List<HoaDonChiTietResponse> chiTietResponses = chiTiets.stream()
                .map(HoaDonChiTietResponse::new)
                .collect(Collectors.toList());

        List<LichSuHoaDon> lichSu = lichSuHoaDonRepository.findAllByHoaDonIdOrderByThoiGianAsc(id);


        return new HoaDonDetailResponse(hoaDon, chiTietResponses, lichSu);
    }

    @Transactional
    public HoaDonResponse updateHoaDon(UUID id, UpdateHoaDonRequest request) {

        String sdt = request.getSdt(); // Lấy SĐT từ DTO/Request
        if (sdt != null && sdt.trim().isEmpty()) {
            sdt = null; // Chuyển chuỗi rỗng ("   ") thành NULL
        }

        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        String loaiHD = hoaDon.getLoaiHoaDon();

        if (loaiHD != null && "online".equalsIgnoreCase(loaiHD.trim())) {
            if (request.getDiaChiGiaoHang() == null || request.getDiaChiGiaoHang().trim().isEmpty()) {
                throw new RuntimeException("Đơn hàng Online bắt buộc phải có địa chỉ giao hàng!");
            }
        }

        hoaDon.setTrangThai(request.getTrangThai());
        hoaDon.setSdt(sdt);
        hoaDon.setDiaChiGiaoHang(request.getDiaChiGiaoHang());

        HoaDon updatedHoaDon = hoaDonRepository.save(hoaDon);

        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(updatedHoaDon);
        lichSu.setThoiGian(LocalDateTime.now());
        lichSu.setNguoiThucHien("admin"); // mặc định
        lichSu.setGhiChu("Cập nhật hóa đơn");

        lichSuHoaDonRepository.save(lichSu);

        return new HoaDonResponse(updatedHoaDon);
    }

    private BigDecimal tinhTongTien(UUID idHoaDon) {
        // 1. Lấy tất cả chi tiết hóa đơn của hóa đơn đó
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepository.findByHoaDonId(idHoaDon);

        if (listHDCT.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal tongTienSauGiamSP = BigDecimal.ZERO;

        // 2. Duyệt qua từng chi tiết hóa đơn
        for (HoaDonChiTiet hdct : listHDCT) {
            // Lấy ChiTietSanPham (CTSP) để truy cập DotGiamGia
            ChiTietSanPham ctsp = hdct.getChiTietSanPham();
            DotGiamGia dgg = ctsp.getDotGiamGia();

            BigDecimal giaBanGoc = hdct.getDonGia(); // Giá bán gốc (trước giảm giá)
            BigDecimal giaTriGiam = BigDecimal.ZERO;

            if (dgg != null && dgg.getTrangThai() == 1) { // Kiểm tra nếu có đợt giảm giá đang hoạt động

                BigDecimal giamPhanTram = new BigDecimal(dgg.getGiaTriGiam())
                        .divide(
                                new BigDecimal(100),
                                4, // Độ chính xác (số chữ số sau dấu thập phân)
                                RoundingMode.HALF_UP // Chế độ làm tròn
                        );

                // Tính số tiền giảm (Giảm theo %)
                BigDecimal soTienGiam = giaBanGoc.multiply(giamPhanTram);

                // Áp dụng giới hạn giảm tối đa
                if (dgg.getSoTienGiamToiDa() != null && soTienGiam.compareTo(dgg.getSoTienGiamToiDa()) > 0) {
                    soTienGiam = dgg.getSoTienGiamToiDa();
                }

                giaTriGiam = soTienGiam;
            }

            // Tính Thành tiền sau khi giảm giá sản phẩm
            BigDecimal thanhTienSauGiamSP = giaBanGoc.subtract(giaTriGiam)
                    .multiply(new BigDecimal(hdct.getSoLuong()));

            // **Cập nhật hoa_don_chi_tiet.thanh_tien** (QUAN TRỌNG: Cột này nên là giá sau giảm SP)
            hdct.setThanhTien(thanhTienSauGiamSP);
            // hoaDonChiTietRepository.save(hdct); // Lưu lại nếu bạn muốn cập nhật cột thanh_tien

            // Cộng dồn vào tổng tiền của hóa đơn
            tongTienSauGiamSP = tongTienSauGiamSP.add(thanhTienSauGiamSP);
        }

        // 3. Trả về tổng tiền sau khi đã giảm giá Sản phẩm
        return tongTienSauGiamSP;
    }
}