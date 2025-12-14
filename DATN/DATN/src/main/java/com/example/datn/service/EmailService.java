package com.example.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender; // Cần cấu hình trong application.properties

    // Địa chỉ email gửi đi (ví dụ: email của cửa hàng)
    private static final String FROM_EMAIL = "noreply@yourdomain.com";

    /**
     * Gửi email chứa mã OTP cho người dùng quên mật khẩu.
     */
    public void sendPasswordResetOtp(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(FROM_EMAIL);
        message.setTo(toEmail);
        message.setSubject("Yêu cầu đặt lại mật khẩu - Mã OTP của bạn");

        String content = String.format(
                "Xin chào,\n\nBạn đã yêu cầu đặt lại mật khẩu.\n" +
                        "Mã OTP của bạn là: %s\n\n" +
                        "Mã này chỉ có hiệu lực trong 5 phút. Vui lòng không chia sẻ mã này với bất kỳ ai.\n\n" +
                        "Trân trọng,\nĐội ngũ hỗ trợ",
                otpCode
        );

        message.setText(content);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email đến " + toEmail + ": " + e.getMessage());
        }
    }
}
