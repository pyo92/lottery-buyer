package com.lottog.buyer.dto.response;

public record LoginResponse(
        Boolean success,
        String message
) {

    public static LoginResponse ok() {
        return new LoginResponse(true, null);
    }

    public static LoginResponse fail(String message) {
        return new LoginResponse(false, message);
    }
}
