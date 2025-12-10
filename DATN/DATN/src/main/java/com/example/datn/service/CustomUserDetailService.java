package com.example.datn.service;

import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final NhanVienRepository nhanVienRepository;
    private final KhachHangRepository khachHangRepository;

    @Override
    public UserDetails loadUserByUsername(String taiKhoan) throws UsernameNotFoundException {

        // ✅ vì repo trả Optional<NhanVien>
        NhanVien nv = nhanVienRepository.findByTaiKhoan(taiKhoan).orElse(null);
        if (nv != null) {
            return nv; // Nv implements UserDetails
        }

        // Khách hàng
        KhachHang kh = khachHangRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + taiKhoan));

        return kh; // Kh implements UserDetails
    }
}
