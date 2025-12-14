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

    private final NhanVienRepository nhanVienRepository;

    public NhanVienDetailsService(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Nếu repo trả Optional
        return nhanVienRepository.findByTaiKhoan(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy nhân viên với tài khoản: " + username)
                );

        // Nếu repo trả entity (không Optional) thì dùng phiên bản này:
        // NhanVien nv = nhanVienRepository.findByTaiKhoan(username);
        // if (nv == null) throw new UsernameNotFoundException("Không tìm thấy nhân viên với tài khoản: " + username);
        // return nv;
    }
}
