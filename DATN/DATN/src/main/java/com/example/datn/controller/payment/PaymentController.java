package com.example.datn.controller.payment;

import com.example.datn.config.VnpayHashUtils;
import com.example.datn.dto.PaymentRequest;
import com.example.datn.service.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Controller
@RequestMapping("/admin/vnpay")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request)
            throws UnsupportedEncodingException {

        String paymentUrl = paymentService.createPaymentUrl(paymentRequest, request);

        return ResponseEntity.ok(
                Map.of("code", "00", "message", "success", "data", paymentUrl)
        );
    }

    @GetMapping("/return")
    public String handleVNPayReturn(HttpServletRequest request) {

        Map<String, String> fields = VnpayHashUtils.getVNPayFields(request);

        if (paymentService.validateHash(fields)) {

            String responseCode = fields.get("vnp_ResponseCode");
            String txnRef = fields.get("vnp_TxnRef");
            String frontendRedirectUrl = "http://localhost:5173/order/status?vnp_TxnRef=" + txnRef;

            if ("00".equals(responseCode)) {
                return "redirect:" + frontendRedirectUrl + "&status=success";
            } else {
                return "redirect:" + frontendRedirectUrl + "&status=failed";
            }
        } else {
            String txnRef = fields.getOrDefault("vnp_TxnRef", "unknown");
            return "redirect:http://localhost:5173/order/status?vnp_TxnRef=" + txnRef + "&status=invalid_signature";
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> body) {
        String txnRef = body.get("txnRef");
        if (txnRef == null) {
            return ResponseEntity.badRequest().body("Missing txnRef");
        }

        return ResponseEntity.ok(Map.of("message", "Payment confirmed"));
    }



}
