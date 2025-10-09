package com.eptiq.vegobike.services;


public interface RazorpayService {
    String createOrder(double amount, String currency) throws Exception;
    boolean verifySignature(String orderId, String paymentId, String signature);
}
