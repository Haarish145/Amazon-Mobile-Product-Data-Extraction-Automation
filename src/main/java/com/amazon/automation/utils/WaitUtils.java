package com.amazon.automation.utils;

import com.amazon.automation.driver.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public final class WaitUtils {
    private WaitUtils() {
    }

    public static WebElement waitForVisible(By locator, Duration timeout) {
        return new WebDriverWait(DriverFactory.getDriver(), timeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static List<WebElement> waitForAllVisible(By locator, Duration timeout) {
        return new WebDriverWait(DriverFactory.getDriver(), timeout)
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }
}
