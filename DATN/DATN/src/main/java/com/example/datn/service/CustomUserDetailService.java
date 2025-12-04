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
        NhanVien nv = nhanVienRepository.findNhanVienByTaiKhoanAndMatKhau(taiKhoan, matKhau).orElse(null);
        if (nv!= null){
            return User.withUsername(nv.getTaiKhoan())
                    .password(nv.getMatKhau())
                    .roles("EMPLOYER")
                    .build();
        }

        KhachHang kh = khachHangRepository.findByTaiKhoanAndMatKhau(taiKhoan, matKhau).orElse(null);
        if (kh != null){
            return User.withUsername(nv.getTaiKhoan())
                    .password(nv.getMatKhau())
                    .roles("GUEST")
                    .build();
        }
        throw new UsernameNotFoundException("Không tìm thấy tài khoản");
    }

}
