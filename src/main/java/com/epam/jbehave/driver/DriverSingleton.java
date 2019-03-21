package com.epam.jbehave.driver;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class DriverSingleton {

    private static WebDriver driver;
    private static final String WEBDRIVER = "webdriver.chrome.driver";
    private static final String WEBDRIVER_EXE_PATH = "src/main/resources/chromedriver.exe";
    private static long WAIT_SECONDS = 30;

    private DriverSingleton() {}

    public static WebDriver getDriver(){
        if (null == driver){
            System.setProperty(WEBDRIVER, WEBDRIVER_EXE_PATH);
            driver = new ChromeDriver();
            driver.manage().timeouts().pageLoadTimeout(WAIT_SECONDS, TimeUnit.SECONDS);
            driver.manage().timeouts().implicitlyWait(WAIT_SECONDS, TimeUnit.SECONDS);
            driver.manage().window().maximize();
        }
        return driver;
    }

    public static void waitVisibilityOf(WebElement element) {
        new WebDriverWait(getDriver(), WAIT_SECONDS).ignoring(StaleElementReferenceException.class).until(ExpectedConditions.visibilityOf(element));
    }

    public static void closeDriver(){
        driver.quit();
        driver = null;
    }
}