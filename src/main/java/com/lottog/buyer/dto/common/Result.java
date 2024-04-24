package com.lottog.buyer.dto.common;

public record Result(
        Boolean success,
        String message
) {

    public static Result of(Boolean success, String message) {
        return new Result(success, message);
    }

    public static Result ok() {
        return Result.of(true, null);
    }

    public static Result fail(String message) {
        return Result.of(false, message);
    }
}
