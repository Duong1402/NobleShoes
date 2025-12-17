package com.example.datn.service.payment;

import com.example.datn.config.VnpayHashUtils;
import com.example.datn.dto.PaymentRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentService {

    @Value("${vnpay.tmncode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashsecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.url}")
    private String vnp_Url;

    @Value("${vnpay.returnurl}")
    private String vnp_ReturnUrl;

    public String getVnpReturnUrl() {
        return vnp_ReturnUrl;
    }

    // Trong PaymentService.java (hoặc VNPayService.java)
    public String createPaymentUrl(PaymentRequest paymentRequest, HttpServletRequest request) throws UnsupportedEncodingException {

        // Tạo mã giao dịch riêng
        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        // Số tiền phải nhân 100
        long amount = paymentRequest.getAmount() * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        String orderInfoTho = paymentRequest.getOrderInfo();
        String orderInfoAnToan = orderInfoTho.replace(" ", "_");

        vnp_Params.put("vnp_OrderInfo", orderInfoAnToan);

        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", paymentRequest.getLanguage());
        vnp_Params.put("vnp_ReturnUrl", this.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr",  getIpAddress(request)); // Tốt nhất nên lấy từ request

        // Định dạng ngày giờ
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // 2. Sắp xếp và tạo Query String / Hash Data
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        boolean first = true;

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {

                if (!first) {
                    hashData.append("&");
                    query.append("&");
                }

                // HASH DATA (KHÔNG encode)
                hashData.append(fieldName)
                        .append("=")
                        .append(fieldValue);

                // QUERY STRING (CÓ encode)
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                        .append("=")
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                first = false;
            }
        }

        // 3. Tạo SecureHash (Checksum)
        String rawHashData = hashData.toString();
        System.out.println("DEBUG RAW HASH DATA: " + rawHashData);
        System.out.println("DEBUG HASH SECRET: [" + vnp_HashSecret + "]");
        String vnp_SecureHash = VnpayHashUtils.hmacSha512(vnp_HashSecret, rawHashData);

        // 4. Build URL cuối cùng
        return vnp_Url + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    public boolean validateHash(Map<String, String> fields) {
        String vnp_SecureHash = fields.get("vnp_SecureHash");
        if (vnp_SecureHash == null) return false;

        String rawHash = VnpayHashUtils.getHashData(fields);
        String secureHash = VnpayHashUtils.hmacSha512(vnp_HashSecret, rawHash);

        return secureHash != null && secureHash.equals(vnp_SecureHash);
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

}
