package com.example.datn.dto;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

public class OtpUtil {
    public static String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public static Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, 5);
        return cal.getTime();
    }
}
