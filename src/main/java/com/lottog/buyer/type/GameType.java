package com.lottog.buyer.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum GameType {

    AUTO("자동"),
    MANUAL("수동"),
    MIX("반자동"),
    NONE("-");

    @Getter
    private final String description;

    GameType(String description) {
        this.description = description;
    }

    public static GameType getLottoType(String description) {
        Optional<GameType> result = Arrays.stream(GameType.values())
                .filter(type -> type.getDescription().equals(description))
                .findAny();

        return result.orElse(GameType.NONE);
    }
}
