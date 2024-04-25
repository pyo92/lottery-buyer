package com.lottog.buyer.dto.common;

import java.util.Map;

public record Data(
        Map<String, Object> map
) {

    public static Data of(Map<String, Object> data) {
        return new Data(data);
    }

    public static Data depositAndBought(Long deposit, Integer bought) {
        return Data.of(Map.of("deposit", deposit, "bought", bought));
    }

    public static Data deposit(Long deposit) {
        return Data.of(Map.of("deposit", deposit));
    }

    public static Data bought(Integer bought) {
        return Data.of(Map.of("bought", bought));
    }

    public static Data payment(String name, String account, Long amount) {
        return Data.of(Map.of("name", name, "account", account, "amount", amount));
    }
}
