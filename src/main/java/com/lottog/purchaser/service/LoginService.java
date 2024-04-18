package com.lottog.purchaser.service;

import com.lottog.purchaser.dto.request.LoginRequest;
import com.lottog.purchaser.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginService {

    @Qualifier("loginInfoMap")
    private final ConcurrentHashMap<String, LoginRequest> loginInfoMap;

    private static final String URL_LOGIN = "https://m.dhlottery.co.kr/user.do?method=loginm&returnUrl=https%3A%2F%2Fm.dhlottery.co.kr%2FuserSsl.do%3Fmethod%3DmyPage";

    private final SeleniumService seleniumService;

    /**
     * 동행복권 로그인 - 최초 시도
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

            //로그인 결과 반환
            LoginResponse response = seleniumService.checkLoginResult();

            if (response.success()) {
                //로그인 성공 시, 로그인 정보 임시 저장
                loginInfoMap.put(request.id(), request);
            } else {
                //로그인 실패 시, chrome driver 종료 및 리소스 반환 처리
                seleniumService.closeWebDriver();
            }

            return response;

        } catch (Exception e) {
            //예외 발생 시, chrome driver 종료 및 리소스 반환 처리
            //로그인만 단독으로 사용되는 경우는 없으므로, 다른 작업이 밀리지 않도록 하기 위해 종료
            seleniumService.closeWebDriver();

            log.error("=== [ERROR] doLogin() - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 동행복권 로그인 - 후행 작업을 위한 method
     * @param id 로그인 id
     * @return 로그인 결과 response DTO
     */
    public LoginResponse login(String id) {
        //map 에서 id 를 이용해 이전 로그인 정보 조회
        LoginRequest loginRequest = loginInfoMap.get(id);

        if (loginRequest == null) {
            return LoginResponse.fail("로그인 정보가 없습니다.");
        }

        return login(loginRequest);
    }
}
