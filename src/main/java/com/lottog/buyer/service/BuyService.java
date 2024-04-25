package com.lottog.buyer.service;

import com.lottog.buyer.dto.common.Game;
import com.lottog.buyer.dto.request.BuyRequest;
import com.lottog.buyer.dto.response.BuyResponse;
import com.lottog.buyer.type.GameType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BuyService {

    private static final String URL_BOUGHT_LIST = "https://dhlottery.co.kr/myPage.do?method=lottoBuyList&searchStartDate={0}&searchEndDate={1}&lottoId=LO40&winGrade=2";

    private static final String URL_LOTTERY_BUY = "https://ol.dhlottery.co.kr/olotto/game/game645.do";

    private final SeleniumService seleniumService;

    /**
     * 당 회차 구매 이력 조회를 통한 구매 매수 반환 method
     * @return 당 회차 구매 매수
     */
    public Integer getBoughtCount() {
        try {
            String css;

            //당 회차(일요일 ~ 토요일) 구매내역 확인창 오픈
            MessageFormat iframeURL = new MessageFormat(URL_BOUGHT_LIST);
            String startDt = getPreviousSunday(); //당 회차 시작일
            String endDt = getNextSaturday(); //당 회차 발표일
            seleniumService.openUrl(iframeURL.format(new Object[] {startDt, endDt}));

            //구매 내역 테이블 조회
            css = "body > table > tbody > tr";
            List<WebElement> purchaseElements = seleniumService.getElementsByCssSelector(css);
            for (WebElement e : purchaseElements) {
                if (e.getText().equals("조회 결과가 없습니다.")) break; //구매 내역이 전혀 없다면, loop exit

                String gameResult = e.findElement(By.cssSelector("td:nth-child(6)")).getText(); //당첨결과 column
                int boughtCount = Integer.parseInt(e.findElement(By.cssSelector("td:nth-child(5)")).getText()); //구입매수 column

                //미추첨 내역은 당 회차 구매내역이므로 잔여 구매가능 매수에서 차감
                if (gameResult.equals("미추첨")) {
                    return boughtCount;
                }
            }

            return 0;

        } catch (Exception e) {
            log.error("=== [ERROR] getBoughtCount() occurred error - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 로또 구매 처리 method
     * @param request 로또 구매 request DTO (유형 + 번호)
     * @return 구매 처리 결과 response DTO (유형 + 번호)
     */
    public BuyResponse buy(BuyRequest request) {
        try {
            //예치금 입금 페이지 이동
            seleniumService.openUrl(URL_LOTTERY_BUY);

            //자동화 툴에 의한 감지로, 비정상 접근 관련 메시지 창을 닫아준다.
            //창이 뜨지 않을 수도 있어서, 예외 처리
            String css = "#popupLayerAlert > div > div.btns > input";
            try {
                seleniumService.getElementByCssSelector(css).click();
            } catch (Exception e) {
                //
            }

            String js;

            for (Game g : request.games()) {
                //자동
                if (g.type().equals(GameType.AUTO)) {
                    js = "$('#checkAutoSelect').click();";
                    seleniumService.execJS(js);

                } else {
                    //수동 + 반자동 숫자 선택
                    for (Integer n : g.numbers()) {
                        //선택한 번호 클릭
                        js = "$('#check645num" + n + "').click();";
                        seleniumService.execJS(js);
                    }

                    //반자동
                    if (g.type().equals(GameType.MIX)) {
                        js = "$('#checkAutoSelect').click();";
                        seleniumService.execJS(js);
                    }
                }

                //게임 추가 버튼 클릭
                js = "$('#btnSelectNum').click();";
                seleniumService.execJS(js);
            }

            //구매 처리
            js = "$('#btnBuy').click();";
            seleniumService.execJS(js);

            //"구매하시겠습니까?" 팝업 윈도우에 대한 "확인" 처리
            js = "closepopupLayerConfirm(true);";
            seleniumService.execJS(js);

            /**
             * 팝업 레이어별 정리 -> 각 레이어의 스타일 중 display: none 이 아닌 것을 찾으면 된다.
             * - #report : 정상 구매 완료
             * - #popupLayerAlert : 번호 미선택, 예치금 부족
             * - #recommend720Plus : 구매한도 초과
             * ---------------------------------------------------------------------
             * 하지만, 성능을 위해 & 우리는 구매 성공 여부만 확인하면 된다.
             * report layer 만 체크함. report layer display none 이라면, 어떤 사유로든 구매실패
             */

            List<Game> games = new ArrayList<>();

            //구매내역 확인 레이어가 출력되어야 정상적으로 구매된 것이므로, 이를 체크
            css = "#report";
            WebElement purchaseResultElement = seleniumService.getElementByCssSelector(css);

            if (purchaseResultElement.getCssValue("display").equals("none")) {
                //구매 실패
                return BuyResponse.fail("더 이상 구매가 불가능합니다.");

            } else { //구매 성공
                css = "#reportRow > li";
                List<WebElement> gameElements = seleniumService.getElementsByCssSelector(css);
                for (WebElement game : gameElements) {
                    //구매 타입 저장
                    String purchaseType = game.findElement(By.cssSelector("strong > span:nth-child(2)")).getText();
                    GameType type = purchaseType.startsWith("자") ? GameType.AUTO :
                                    (purchaseType.startsWith("수") ? GameType.MANUAL : GameType.MIX);

                    //구매 번호 저장
                    List<Integer> numbers = game.findElements(By.cssSelector("div.nums > span"))
                            .stream()
                            .map(e -> Integer.parseInt(e.getText()))
                            .toList();

                    games.add(Game.of(type, numbers));
                }

                return BuyResponse.ok(games);
            }

        } catch (Exception e) {
            log.error("=== [ERROR] buy() occurred error - {}", e.getMessage());
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
