package com.lottog.purchaser.dto.request;

public record PaymentRequest(
        String id
) {

    public static PaymentRequest of(String id) {
        return new PaymentRequest(id);
    }
}
