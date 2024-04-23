package com.lottog.buyer.service;

import com.lottog.buyer.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DepositService {

    private static final String URL_DEPOSIT_PAYMENT = "https://m.dhlottery.co.kr/payment.do?method=payment";

    private final SeleniumService seleniumService;

    /**
     * 예치금 잔액 조회
     * @return 예치금
     */
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
            log.error("=== [ERROR] getDeposit() occurred error - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 예치금 입금 신청
     * @return 입금 신청 결과 DTO (계좌번호 등)
     */
    public PaymentResponse payment() {
        try {
            //예치금 입금 페이지 이동
            seleniumService.openUrl(URL_DEPOSIT_PAYMENT);

            //고정 가상계좌 입금 선택 (2023.09.02 추가)
            String css = "#container > div > div.tab_ec > a:nth-child(2)";
            seleniumService.getElementByCssSelector(css).click();

            css = "#Amt";
            Select amountSelect = new Select(seleniumService.getElementByCssSelector(css));
            amountSelect.selectByValue("5000"); //회차당 구매가능 게임이 5게임이므로 5000원으로 고정

            //예치금 입금 신청
            String js = "nicepayStart();";
            seleniumService.execJS(js);

            //예치금 충전 정보 테이블 조회
            css = "#container > div > div.complete_content > div > table > tbody > tr";
            List<WebElement> depositInfo = seleniumService.getElementsByCssSelector(css);
            String accountName = depositInfo.get(2).findElement(By.cssSelector("td")).getText();
            String accountNumber = depositInfo.get(3).findElement(By.cssSelector("td > span")).getText();
            Long depositAmount = 5000L;

            return PaymentResponse.of(accountName, accountNumber, depositAmount);

        } catch (Exception e) {
            log.error("=== [ERROR] payment() occurred error - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
