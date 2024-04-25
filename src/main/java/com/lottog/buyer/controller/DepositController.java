package com.lottog.buyer.controller;

import com.lottog.buyer.dto.common.Result;
import com.lottog.buyer.dto.request.PaymentRequest;
import com.lottog.buyer.dto.response.DepositResponse;
import com.lottog.buyer.dto.response.ErrorResponse;
import com.lottog.buyer.dto.response.PaymentResponse;
import com.lottog.buyer.service.DepositService;
import com.lottog.buyer.service.LoginService;
import com.lottog.buyer.service.SeleniumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            Result loginResult = loginService.login(id);

            //로그인 실패 시, 결과 반환 처리
            if (!loginResult.success()) {
                return ResponseEntity.ok(DepositResponse.fail(loginResult.message()));
            }

            return ResponseEntity.ok(depositService.getDeposit());

        } catch (Exception e) {
            log.error("=== [ERROR] getDeposit() occurred error - {}", e.getMessage());
            ErrorResponse response = ErrorResponse.status500("getDeposit() - 오류가 발생했습니다. (id = " + id + ") - " + e.getMessage());

            return ResponseEntity
                    .status(response.status())
                    .body(response);

        } finally {
            seleniumService.closeWebDriver();
        }
    }

    @ResponseBody
    @PostMapping("/payment")
    public ResponseEntity<?> payment(@RequestBody PaymentRequest request) {
        try {
            //로그인 처리
            Result loginResult = loginService.login(request.id());

            //로그인 실패 시, 결과 반환 처리
            if (!loginResult.success()) {
                return ResponseEntity.ok(PaymentResponse.fail(loginResult.message()));
            }

            return ResponseEntity.ok(depositService.payment(request.amount()));

        } catch (Exception e) {
            log.error("=== [ERROR] payment() occurred error - {}", e.getMessage());
            ErrorResponse response = ErrorResponse.status500("payment() - 오류가 발생했습니다. (id = " + request.id() + ") - " + e.getMessage());

            return ResponseEntity
                    .status(response.status())
                    .body(response);

        } finally {
            seleniumService.closeWebDriver();
        }
    }
}
