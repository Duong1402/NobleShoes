package com.example.datn.service;

import com.example.datn.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Primary
@Service("khachHangDetailsService")
public class KhachHangDetailsService implements UserDetailsService {
    @Autowired
    private KhachHangRepository khachHangRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return khachHangRepository.findByTaiKhoan(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy Khách Hàng: " + username));
    }
}