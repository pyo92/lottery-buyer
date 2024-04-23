package com.lottog.buyer.dto.response;

public record UserInfoResponse(
        Boolean purchasable,
        String message,
        Long deposit,
        Integer pc //purchasable count
) {

    public static UserInfoResponse ok(Long deposit, Integer pc) {
        return new UserInfoResponse(true, null, deposit, pc);
    }

    public static UserInfoResponse fail(String message, Long deposit, Integer pc) {
        return new UserInfoResponse(false, message, deposit, pc);
    }
}
