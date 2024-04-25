package com.lottog.buyer.dto.request;

public record PaymentRequest(
        String id,
        Long amount
) {

    public static PaymentRequest of(String id, Long amount) {
        return new PaymentRequest(id, amount);
    }
}
