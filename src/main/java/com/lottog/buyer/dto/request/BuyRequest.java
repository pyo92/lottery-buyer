package com.lottog.buyer.dto.request;

import com.lottog.buyer.dto.common.Game;

import java.util.List;

public record BuyRequest(
        String id,
        List<Game> games
) {

    public static BuyRequest of(String id, List<Game> games) {
        return new BuyRequest(id, games);
    }
}