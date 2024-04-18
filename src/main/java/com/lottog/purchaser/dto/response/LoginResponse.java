package com.lottog.purchaser.dto.response;

import org.springframework.http.HttpStatus;

public record LoginResponse(
        HttpStatus status,
        Boolean success,
        String message
) {

    public static LoginResponse ok() {
        return new LoginResponse(HttpStatus.OK, true, null);
    }

    public static LoginResponse fail(String message) {
        return new LoginResponse(HttpStatus.OK, false, message);
    }
}
