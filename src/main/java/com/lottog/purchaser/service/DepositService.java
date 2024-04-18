package com.lottog.purchaser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DepositService {

    private final SeleniumService seleniumService;

    public Long getDeposit() {
        try {
            String css;

            //예치금 잔액 조회
            css = "#container > div > div.myinfo_content.account > div.deposit > span > strong";

            //예치금 잔액 반환
            return Long.parseLong(
                    seleniumService
                            .getElementByCssSelector(css)
                            .getText()
                            .replaceAll("[^0-9]", "")
            );

        } catch (Exception e) {
            seleniumService.closeWebDriver();

            log.error("=== deposit() occurred error - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
