package com.lottog.purchaser.controller;

import com.lottog.purchaser.dto.response.ErrorResponse;
import com.lottog.purchaser.dto.response.LoginResponse;
import com.lottog.purchaser.service.DepositService;
import com.lottog.purchaser.service.LoginService;
import com.lottog.purchaser.service.SeleniumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DepositController {

    private final SeleniumService seleniumService;

    private final LoginService loginService;

    private final DepositService depositService;

    @ResponseBody
    @GetMapping("/deposit")
    public ResponseEntity<?> getDeposit(String id) {
        try {
            //로그인 처리
            LoginResponse loginResponse = loginService.login(id);

            //로그인 실패 시, 결과 반환 처리
            if (!loginResponse.success()) {
                return ResponseEntity.ok(loginResponse);
            }

            Long deposit = depositService.getDeposit();

            return ResponseEntity.ok(Map.of("deposit", deposit));

        } catch (Exception e) {
            log.error("=== getDeposit() occurred error - {}", e.getMessage());
            ErrorResponse response = ErrorResponse.status500("getDeposit() - 오류가 발생했습니다. (id = " + id + ") - " + e.getMessage());

            return ResponseEntity
                    .status(response.status())
                    .body(response);

        } finally {
            seleniumService.closeWebDriver();
        }
    }
}
