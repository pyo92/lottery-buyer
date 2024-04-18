package com.lottog.purchaser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchasableCountService {

    private static final String URL_PURCHASED_LIST = "https://dhlottery.co.kr/myPage.do?method=lottoBuyList&searchStartDate={0}&searchEndDate={1}&lottoId=LO40&winGrade=2";

    private final SeleniumService seleniumService;

    /**
     * 당 회차 구매 이력 조회를 통한 구매가능 매수 반환 method
     * @return 구매가능 매수
     */
    public Integer getPurchasableCount() {
        try {
            String css;
            int pc = 5; //온라인 구매 제한 = 5게임

            //당 회차(일요일 ~ 토요일) 구매내역 확인창 오픈
            MessageFormat iframeURL = new MessageFormat(URL_PURCHASED_LIST);
            String startDt = getPreviousSunday(); //당 회차 시작일
            String endDt = getNextSaturday(); //당 회차 발표일
            seleniumService.openUrl(iframeURL.format(new Object[] {startDt, endDt}));

            //구매 내역 테이블 조회
            css = "body > table > tbody > tr";
            List<WebElement> purchaseElements = seleniumService.getElementsByCssSelector(css);
            for (WebElement e : purchaseElements) {
                if (e.getText().equals("조회 결과가 없습니다.")) break; //구매 내역이 전혀 없다면, loop exit

                String gameResult = e.findElement(By.cssSelector("td:nth-child(6)")).getText(); //당첨결과 column
                int purchaseCnt = Integer.parseInt(e.findElement(By.cssSelector("td:nth-child(5)")).getText()); //구입매수 column

                //미추첨 내역은 당 회차 구매내역이므로 잔여 구매가능 매수에서 차감
                if (gameResult.equals("미추첨")) {
                    pc -= purchaseCnt;
                }
            }

            return pc;

        } catch (Exception e) {
            seleniumService.closeWebDriver();

            log.error("=== getPurchasableCount() occurred error - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 당 회차 시작일 반환
     * @return 당 회차 시작일 (yyyyMMdd)
     */
    private String getPreviousSunday() {
        LocalDate today = LocalDate.now();

        //오늘의 DayOfWeek 더해준다. (오늘을 포함한 과거의 가장 가까운 일요일) - 회차 시작일
        return today.minusDays(today.getDayOfWeek().getValue())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 당 회차 발표일 반환
     * @return 당 회차 발표일 (yyyyMMdd)
     */
    private String getNextSaturday() {
        LocalDate today = LocalDate.now();

        //토요일이 6 이므로, 오늘의 DayOfWeek 빼준다. (오늘을 포함한 미래의 가장 가까운 토요일) - 회차 종료일
        return today.plusDays(6 - today.getDayOfWeek().getValue())
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
