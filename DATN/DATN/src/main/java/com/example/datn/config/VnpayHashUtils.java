package com.example.datn.config;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VnpayHashUtils {

    public static String hmacSha512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new IllegalArgumentException("Key or data must not be null");
            }

            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSHA512.init(secretKey);

            byte[] hash = hmacSHA512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static String getHashData(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = fields.get(fieldName);

            // Chỉ hash các tham số VNPay, bỏ qua tham số SecureHash
            if (fieldValue != null && fieldValue.length() > 0 && !fieldName.equals("vnp_SecureHash")) {
                hashData.append(fieldName).append("=").append(fieldValue);

                if (i < fieldNames.size() - 1) {
                    hashData.append("&");
                }
            }
        }
        return hashData.toString();
    }

    // ✅ đổi javax -> jakarta
    public static Map<String, String> getVNPayFields(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);

            if (paramValue != null && !paramValue.isEmpty()) {
                fields.put(paramName, paramValue);
            }
        }
        return fields;
    }
}