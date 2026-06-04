package com.amazon.automation.driver;

import com.amazon.automation.config.FrameworkConfig;
import com.amazon.automation.constants.FrameworkConstants;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public final class DriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void initializeDriver() {
        String browser = FrameworkConfig.getOrDefault("browser", FrameworkConstants.DEFAULT_BROWSER);
        if (!"chrome".equalsIgnoreCase(browser)) {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        options.addArguments("--start-maximized");
        if (FrameworkConfig.getBoolean("headless", false)) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        DRIVER.set(driver);
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("Driver is not initialized for the current thread");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
