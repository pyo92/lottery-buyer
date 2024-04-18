package com.lottog.purchaser.dto.response;

public record PaymentResponse(
        String name,
        String account,
        Long amount
) {

    public static PaymentResponse of(String name, String account, Long amount) {
        return new PaymentResponse(name, account, amount);
    }
}
