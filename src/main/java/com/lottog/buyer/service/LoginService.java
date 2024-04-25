package com.lottog.buyer.service;

import com.lottog.buyer.dto.common.Result;
import com.lottog.buyer.dto.request.LoginRequest;
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
    public Result login(LoginRequest request) {
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
            Result result = seleniumService.checkLoginResult();

            //로그인 성공 시, 로그인 정보 임시 저장
            if (result.success()) {
                loginInfoMap.put(request.id(), request);
            }

            return result;

        } catch (Exception e) {
            log.error("=== [ERROR] doLogin() - {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 동행복권 로그인 - 후행 작업을 위한 method
     * @param id 로그인 id
     * @return 로그인 결과 response DTO
     */
    public Result login(String id) {
        //map 에서 id 를 이용해 이전 로그인 정보 조회
        LoginRequest loginRequest = loginInfoMap.get(id);

        if (loginRequest == null) {
            return Result.fail("로그인 정보가 없습니다.");
        }

        return login(loginRequest);
    }
}
