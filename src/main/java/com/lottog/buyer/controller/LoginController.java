package com.lottog.buyer.controller;

import com.lottog.buyer.dto.common.Result;
import com.lottog.buyer.dto.request.LoginRequest;
import com.lottog.buyer.dto.response.ErrorResponse;
import com.lottog.buyer.dto.response.LoginResponse;
import com.lottog.buyer.service.BuyService;
import com.lottog.buyer.service.DepositService;
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
public class LoginController {

    private final SeleniumService seleniumService;

    private final LoginService loginService;

    private final DepositService depositService;

    private final BuyService buyService;

    @ResponseBody
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            //로그인 처리
            Result loginResult = loginService.login(request);

            //로그인 실패 시, 결과 반환 처리
            if (!loginResult.success()) {
                return ResponseEntity.ok(LoginResponse.fail(loginResult.message()));
            }

            //사용자 정보 (예치금 잔액, 구매 가능 게임 수) 조회
            Long deposit = (Long) depositService.getDeposit()
                    .data()
                    .get("deposit");

            Integer bought = buyService.getBoughtCount();

            if (bought == 5) {
                return ResponseEntity.ok(LoginResponse.fail("더 이상 구매할 수 없습니다.", bought));
            }

            //결과 반환
            return ResponseEntity.ok(LoginResponse.ok(deposit, bought));

        } catch (Exception e) {
            log.error("=== [ERROR] login() occurred error - {}", e.getMessage());
            ErrorResponse response = ErrorResponse.status500("login() - 오류가 발생했습니다. (id = " + request.id() + ") - " + e.getMessage());

            return ResponseEntity
                    .status(response.status())
                    .body(response);

        } finally {
            seleniumService.closeWebDriver();
        }
    }
}
