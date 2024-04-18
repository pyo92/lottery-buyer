package com.lottog.purchaser.dto.request;

public record LoginRequest(
        String id,
        String pw
) {

    public static LoginRequest of(String id, String pw) {
        return new LoginRequest(id, pw);
    }
}
