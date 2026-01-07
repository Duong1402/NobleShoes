package com.example.datn.service;

import com.cloudinary.api.exceptions.NotFound;
import com.example.datn.entity.KhachHang;
import com.example.datn.entity.NhanVien;
import com.example.datn.repository.KhachHangRepository;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailService {
    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    public UserDetails loadUserByTaiKhoan(String taiKhoan, String matKhau) throws UsernameNotFoundException{

        NhanVien nv = nhanVienRepository.findByTaiKhoan(taiKhoan).orElse(null);
        if (nv != null) {
            return nv;
        }


        KhachHang kh = khachHangRepository.findByTaiKhoan(taiKhoan)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + taiKhoan));

        return kh;
    }

}
