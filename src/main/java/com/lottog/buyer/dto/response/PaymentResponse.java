package com.lottog.buyer.dto.response;

import com.lottog.buyer.dto.common.Data;
import com.lottog.buyer.dto.common.Result;

import java.util.Map;

public record PaymentResponse(
        Result result,
        Map<String, Object> data
) {

    public static PaymentResponse of(Result result, Map<String, Object> data) {
        return new PaymentResponse(result, data);
    }

    public static PaymentResponse ok(String name, String account, Long amount) {
        return PaymentResponse.of(Result.ok(), Data.payment(name, account, amount).map());
    }

    public static PaymentResponse fail(String message) {
        return PaymentResponse.of(Result.fail(message), null);
    }
}
