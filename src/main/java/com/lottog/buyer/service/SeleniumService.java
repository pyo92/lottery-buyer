package com.lottog.buyer.service;

import com.lottog.buyer.dto.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class SeleniumService {

    @Value("${selenium.hub.url}")
    private String SELENIUM_HUB_URL;

    @Value("${selenium.retry.count}")
    private int SELENIUM_RETRY_CNT;

    private WebDriver webDriver;

    private WebDriverWait webDriverWait;

    /**
     * Open chrome driver
     */
    public void openWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--window-size=1024,768");
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-extensions");
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("--disable-blink-features=AutomationControlled");
        //Access strategy - eager access DOM elements before fully loading
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        try {
            webDriver = new RemoteWebDriver(new URL(SELENIUM_HUB_URL), options);
            webDriverWait = new WebDriverWait(webDriver, Duration.ofMillis(100)); //최대 100ms 대기

        } catch (MalformedURLException e) {
            log.error("=== [Error] Exception occur to open web driver", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Close chrome driver
     */
    public void closeWebDriver() {
        try {
            if (webDriver != null) {
                webDriver.quit();
            }

        } catch (Exception e) {
            log.error("=== [Error] Exception occur to close web driver", e);
            throw new RuntimeException(e);

        } finally {
            webDriver = null;
            webDriverWait = null;
        }
    }

    /**
     * Open URL
     * @param url URL 주소
     */
    public void openUrl(String url) {
        try {
            execJS("location.href = \"" + url + "\""); //URL 이동

            //메인 외에 모든 다른 창 닫기
            String main = webDriver.getWindowHandle();
            for (String handle : webDriver.getWindowHandles()) {
                if(!handle.equals(main)) {
                    webDriver.switchTo().window(handle).close();
                }
            }

            webDriver.switchTo().window(main);

        } catch (Exception e) {
            log.error("=== [Error] Exception occur to open URL - (URL = {})", url);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get web element by CSS selector (단일 객체)
     * @param css CSS selector
     * @return Web element
     */
    public WebElement getElementByCssSelector(String css) {
        for (int i = 0; i < SELENIUM_RETRY_CNT; i++) {
            try {
                return webDriverWait.until(
                        ExpectedConditions.presenceOfElementLocated(By.cssSelector(css))
                );

            } catch (TimeoutException e) {
                log.warn("=== [WARNING] Css selector `{}` not found, retrying... ({}/{})", css, (i + 1), SELENIUM_RETRY_CNT);
            }
        }

        throw new NoSuchElementException("Css selector `" + css + "` not found.");
    }

    /**
     * Get web element by CSS selector (다중 객체)
     * @param css CSS selector
     * @return List of web element
     */
    public List<WebElement> getElementsByCssSelector(String css) {
        for (int i = 0; i < SELENIUM_RETRY_CNT; i++) {
            try {
                return webDriverWait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(css))
                );

            } catch (TimeoutException e) {
                log.warn("=== Css selector `{}` not found, retrying... ({}/{})", css, (i + 1), SELENIUM_RETRY_CNT);
            }
        }

        throw new NoSuchElementException("Css selector `" + css + "` not found.");
    }

    /**
     * Execute java script
     * @param script Java script
     */
    public void execJS(String script) {
        try {
            ((JavascriptExecutor) webDriver).executeScript(script);
            Thread.sleep(100); //Java script executor 는 적절한 wait 옵션이 없어서 thread sleep 처리

        } catch (Exception e) {
            throw new JavascriptException("Java script `" + script + "` execute failed.");
        }
    }

    /**
     * 동행복권 로그인 결과 체크 (실패 시, alert display = block)
     * @return 로그인 결과 response DTO
     */
    public LoginResponse checkLoginResult() {
        try {
            //로그인 실패 시, alert message 를 저장하고, accept 처리
            Alert alert = webDriver.switchTo().alert();
            String result = alert.getText();
            alert.accept();

            return LoginResponse.fail(result);

        } catch (Exception e) {
            //로그인 실패 시에만 alert 가 표시된다. (display = block)
            //로그인 성공 시를 대비한 의도적인 empty try-catch block 이다.
        }

        return LoginResponse.ok();
    }
}
