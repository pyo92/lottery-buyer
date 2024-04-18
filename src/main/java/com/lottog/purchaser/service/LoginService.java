package com.lottog.purchaser.service;

import com.lottog.purchaser.dto.request.LoginRequest;
import com.lottog.purchaser.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {

    private static final String URL_LOGIN = "https://m.dhlottery.co.kr/user.do?method=loginm&returnUrl=https%3A%2F%2Fm.dhlottery.co.kr%2FuserSsl.do%3Fmethod%3DmyPage";

    private final SeleniumService seleniumService;

    /**
     * 동행복권 로그인
     * @param request 로그인 request DTO
     * @return 로그인 결과 response DTO
     */
    public LoginResponse login(LoginRequest request) {
        try {
            seleniumService.openWebDriver();
            seleniumService.openUrl(URL_LOGIN);

            String js;
            String css;

            //아이디 입력
            css = "#userId";
            WebElement idElement = seleniumService.getElementByCssSelector(css);
            idElement.sendKeys(request.id());

            //비밀번호 입력
            css = "#password";
            WebElement passwordElement = seleniumService.getElementByCssSelector(css);
            passwordElement.sendKeys(request.pw());

            //로그인 시도
            js = "check_if_Valid3();";
            seleniumService.execJS(js);

            return seleniumService.checkLoginResult(); //로그인 결과 반환

        } catch (Exception e) {
            log.error("=== [ERROR] doLogin() - {}", e.getMessage());
            throw new RuntimeException(e);

        } finally {
            seleniumService.closeWebDriver(); //성공이든 실패든 chrome driver 를 닫고 리소스를 반납한다.
        }
    }
}
