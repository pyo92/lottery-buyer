package com.lottog.buyer.dto.common;

import com.lottog.buyer.type.GameType;

import java.util.List;

public record Game(
        GameType type,
        List<Integer> numbers
) {

    public static Game of(GameType type, List<Integer> numbers) {
        return new Game(type, numbers);
    }
}
