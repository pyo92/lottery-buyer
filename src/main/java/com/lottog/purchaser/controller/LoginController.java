package com.lottog.purchaser.controller;

import com.lottog.purchaser.dto.request.LoginRequest;
import com.lottog.purchaser.dto.response.ErrorResponse;
import com.lottog.purchaser.dto.response.LoginResponse;
import com.lottog.purchaser.dto.response.UserInfoResponse;
import com.lottog.purchaser.service.LoginService;
import com.lottog.purchaser.service.DepositService;
import com.lottog.purchaser.service.PurchasableCountService;
import com.lottog.purchaser.service.SeleniumService;
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

    private final PurchasableCountService purchasableCountService;

    @ResponseBody
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            //로그인 처리
            LoginResponse loginResponse = loginService.login(request);

            //로그인 실패 시, 결과 반환 처리
            if (!loginResponse.success()) {
                return ResponseEntity.ok(loginResponse);
            }

            //사용자 정보 (예치금 잔액, 구매 가능 게임 수) 조회
            Long deposit = depositService.getDeposit();
            Integer purchasableCount = purchasableCountService.getPurchasableCount();

            UserInfoResponse response;

            if (purchasableCount == 0) {
                response = UserInfoResponse.fail("더 이상 구매할 수 없습니다.", deposit, purchasableCount);
            } else {
                response = UserInfoResponse.ok(deposit, purchasableCount);
            }

            //결과 반환
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("=== login() occurred error - {}", e.getMessage());
            ErrorResponse response = ErrorResponse.status500("login() - 오류가 발생했습니다. (id = " + request.id() + ") - " + e.getMessage());

            return ResponseEntity
                    .status(response.status())
                    .body(response);

        } finally {
            seleniumService.closeWebDriver();
        }
    }
}
