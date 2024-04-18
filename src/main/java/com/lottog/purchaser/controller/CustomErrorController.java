package com.lottog.purchaser.controller;

import com.lottog.purchaser.dto.response.ErrorResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError() {
        ErrorResponse response = ErrorResponse.status400("올바르지 않은 접근입니다.");

        return ResponseEntity
                .status(response.status())
                .body(response);
    }
}
