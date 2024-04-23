package com.lottog.buyer.dto.response;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        HttpStatus status,
        String message
) {

    private static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(status, "[ERROR] " + message);
    }

    public static ErrorResponse status400(String message) {
        return ErrorResponse.of(HttpStatus.NOT_FOUND, message);
    }

    public static ErrorResponse status500(String message) {
        return ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
