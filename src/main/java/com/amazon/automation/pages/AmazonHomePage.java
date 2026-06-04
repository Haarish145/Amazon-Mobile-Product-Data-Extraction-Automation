package com.amazon.automation.pages;

import com.amazon.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class AmazonHomePage {
    private final WebDriver driver;
    private final By searchBox = By.id("twotabsearchtextbox");
    private final By searchButton = By.id("nav-search-submit-button");

    public AmazonHomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl);
        WaitUtils.waitForVisible(searchBox, Duration.ofSeconds(20));
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public void searchFor(String keyword) {
        WaitUtils.waitForVisible(searchBox, Duration.ofSeconds(20)).clear();
        WaitUtils.waitForVisible(searchBox, Duration.ofSeconds(20)).sendKeys(keyword);
        WaitUtils.waitForVisible(searchButton, Duration.ofSeconds(20)).click();
    }
}
