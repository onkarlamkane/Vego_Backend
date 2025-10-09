package com.eptiq.vegobike.services.impl;

import com.eptiq.vegobike.services.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class RazorpayServiceImpl implements RazorpayService {
    private final RazorpayClient client;
    private final String secret;

    public RazorpayServiceImpl(
            @Value("${razorpay.key_id}") String key,
            @Value("${razorpay.key_secret}") String secret
    ) throws Exception {
        this.client = new RazorpayClient(key, secret);
        this.secret = secret;
    }

    @Override
    public String createOrder(double amount, String currency) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100)); // amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("payment_capture", 1);
        Order order = client.orders.create(orderRequest);
        return order.toString();
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String payload = orderId + "|" + paymentId;
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
            String generatedSignatureHex = bytesToHex(hash);
            return generatedSignatureHex.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
