package com.lottog.purchaser.controller;

import com.lottog.purchaser.dto.request.LoginRequest;
import com.lottog.purchaser.dto.response.ErrorResponse;
import com.lottog.purchaser.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @ResponseBody
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(loginService.login(request));

        } catch (Exception e) {
            ErrorResponse response = ErrorResponse.status500("login() - 오류가 발생했습니다. (id = " + request.id() + ") - " + e.getMessage());

            return ResponseEntity
                    .status(response.status())
                    .body(response);
        }
    }
}
