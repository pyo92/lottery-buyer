package com.lottog.buyer.dto.response;

import com.lottog.buyer.dto.common.Data;
import com.lottog.buyer.dto.common.Result;

import java.util.Map;

public record DepositResponse(
        Result result,
        Map<String, Object> data
) {

    public static DepositResponse of(Result result, Map<String, Object> data) {
        return new DepositResponse(result, data);
    }

    public static DepositResponse ok(Long deposit) {
        return DepositResponse.of(Result.ok(), Data.deposit(deposit).map());
    }

    public static DepositResponse fail(String message) {
        return DepositResponse.of(Result.fail(message), null);
    }
}
