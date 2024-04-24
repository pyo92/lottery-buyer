package com.lottog.buyer.controller;

import com.lottog.buyer.dto.request.BuyRequest;
import com.lottog.buyer.dto.response.ErrorResponse;
import com.lottog.buyer.dto.response.LoginResponse;
import com.lottog.buyer.service.BuyService;
import com.lottog.buyer.service.LoginService;
import com.lottog.buyer.service.SeleniumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BuyController {

    private final SeleniumService seleniumService;

    private final LoginService loginService;

    private final BuyService buyService;

    @ResponseBody
    @PostMapping("/buy")
    public ResponseEntity<?> buy(@RequestBody BuyRequest request) {
        try {
            //로그인 처리
            LoginResponse loginResponse = loginService.login(request.id());

            //로그인 실패 시, 결과 반환 처리
            if (!loginResponse.success()) {
                return ResponseEntity.ok(loginResponse);
            }

            return ResponseEntity.ok(buyService.buy(request));

        } catch (Exception e) {
            log.error("=== [ERROR] buy() occurred error - {}", e.getMessage());
            ErrorResponse response = ErrorResponse.status500("buy() - 오류가 발생했습니다. (id = " + request.id() + ") - " + e.getMessage());

            return ResponseEntity
                    .status(response.status())
                    .body(response);

        } finally {
            seleniumService.closeWebDriver();
        }
    }
}
