package com.amazon.automation.pages;

import com.amazon.automation.exceptions.CaptchaDetectedException;
import com.amazon.automation.model.ProductData;
import com.amazon.automation.utils.WaitUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AmazonSearchResultsPage {
    private final WebDriver driver;
    private final By productCards = By.cssSelector("div[data-component-type='s-search-result']");
    private final By noResultsBanner = By.cssSelector("div.s-no-results-section");

    public AmazonSearchResultsPage(WebDriver driver) {
        this.driver = driver;
    }

    public List<ProductData> extractProducts(int maxProducts) {
        if (driver.getTitle().contains("Robot Check")
                || driver.getPageSource().contains("Enter the characters you see below")) {
            throw new CaptchaDetectedException("Amazon CAPTCHA detected. Execution is blocked.");
        }

        if (!driver.findElements(noResultsBanner).isEmpty()) {
            return List.of();
        }

        List<WebElement> cards = WaitUtils.waitForAllVisible(productCards, Duration.ofSeconds(25));
        List<ProductData> products = new ArrayList<>();
        for (WebElement card : cards) {
            if (products.size() >= maxProducts) {
                break;
            }

            String productName = safeText(card, By.cssSelector("h2 span"), "N/A");
            if (StringUtils.isBlank(productName) || "N/A".equals(productName)) {
                continue;
            }
            String whole = safeText(card, By.cssSelector("span.a-price-whole"), "N/A");
            String fraction = safeText(card, By.cssSelector("span.a-price-fraction"), "00");
            String price = "N/A".equals(whole) ? "N/A" : whole + "." + fraction;
            String rating = safeText(card, By.cssSelector("span.a-icon-alt"), "N/A");
            String reviewsCount = safeText(card, By.cssSelector("span[aria-label$='ratings']"), "N/A");
            if ("N/A".equals(reviewsCount)) {
                reviewsCount = safeText(card, By.cssSelector("div.a-row.a-size-small span"), "N/A");
            }
            String primeBadge = card.findElements(By.cssSelector("i[aria-label='Prime']")).isEmpty() ? "No" : "Yes";
            products.add(new ProductData(productName, price, rating, reviewsCount, primeBadge));
        }
        return products;
    }

    private String safeText(WebElement rootElement, By locator, String fallback) {
        try {
            String value = rootElement.findElement(locator).getText().trim();
            return value.isBlank() ? fallback : value;
        } catch (NoSuchElementException exception) {
            return fallback;
        }
    }
}
