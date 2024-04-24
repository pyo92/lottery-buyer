package com.lottog.buyer.dto.response;

import com.lottog.buyer.dto.common.Game;
import com.lottog.buyer.dto.common.Result;

import java.util.List;

public record BuyResponse(
        Result result,
        List<Game> data
) {

    public static BuyResponse of(Result result, List<Game> data) {
        return new BuyResponse(result, data);
    }

    public static BuyResponse ok(List<Game> data) {
        return BuyResponse.of(Result.ok(), data);
    }

    public static BuyResponse fail(String message) {
        return BuyResponse.of(Result.fail(message), null);
    }
}
