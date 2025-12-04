package com.example.datn.service;

import com.example.datn.entity.NhanVien;
import com.example.datn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("nhanVienDetailsService")
public class NhanVienDetailsService implements UserDetailsService {
    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NhanVien nv = nhanVienRepository.findByTaiKhoan(username);
        if (nv == null){
            throw new RuntimeException("Lỗi hệ thống: Không tìm thấy nhân viên.");
        }
        return nv;
    }
}
