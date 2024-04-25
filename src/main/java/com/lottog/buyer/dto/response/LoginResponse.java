package com.lottog.buyer.dto.response;

import com.lottog.buyer.dto.common.Data;
import com.lottog.buyer.dto.common.Result;

import java.util.Map;

public record LoginResponse(
        Result result,
        Map<String, Object> data
) {

    public static LoginResponse of(Result result, Map<String, Object> data) {
        return new LoginResponse(result, data);
    }

    public static LoginResponse ok(Long deposit, Integer bought) {
        return LoginResponse.of(Result.ok(), Data.depositAndBought(deposit, bought).map());
    }

    public static LoginResponse fail(String message, Integer bought) {
        return LoginResponse.of(Result.fail(message), Data.bought(bought).map());
    }

    public static LoginResponse fail(String message) {
        return LoginResponse.of(Result.fail(message), null);
    }
}
