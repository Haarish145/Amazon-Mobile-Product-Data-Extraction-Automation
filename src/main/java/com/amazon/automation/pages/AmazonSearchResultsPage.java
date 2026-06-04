package com.amazon.automation.pages;

import com.amazon.automation.exceptions.CaptchaDetectedException;
import com.amazon.automation.model.ProductData;
import com.amazon.automation.utils.WaitUtils;
import com.amazon.automation.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    // primary and fallback locators to improve resilience
    private final By productCardsFallback = By.cssSelector("div.s-result-item[data-asin]");
    private final By productCards = By.cssSelector("div[data-component-type='s-search-result']");
    private final By noResultsBanner = By.cssSelector("div.s-no-results-section");
    private final By noResultsText = By.xpath("//div[contains(text()," + "\"didn't match any products\"" + ") or contains(., 'No results for')]");
    private static final Logger logger = LogManager.getLogger(AmazonSearchResultsPage.class);

    public AmazonSearchResultsPage(WebDriver driver) {
        this.driver = driver;
    }

    public List<ProductData> extractProducts(String keyword, int maxProducts) {
        // Detect common CAPTCHA/robot blocks and capture a screenshot for diagnostics
        try {
            String pageTitle = driver.getTitle() == null ? "" : driver.getTitle();
            String pageSource = driver.getPageSource() == null ? "" : driver.getPageSource();
            if (pageTitle.toLowerCase().contains("robot check")
                    || pageSource.contains("Enter the characters you see below")
                    || pageSource.contains("Type the characters you see in the image")) {
                String screenshot = ScreenshotUtils.capture("captcha_blocked", keyword);
                logger.error("CAPTCHA detected - screenshot saved at {}", screenshot);
                throw new CaptchaDetectedException("Amazon CAPTCHA detected. Screenshot: " + screenshot);
            }
        } catch (CaptchaDetectedException cex) {
            throw cex;
        } catch (Exception ignore) {
            // Best effort detection; continue if we can't access title/source
        }

        // Detect no-results and capture screenshot for analysis
        try {
            boolean noResults = !driver.findElements(noResultsBanner).isEmpty() || !driver.findElements(noResultsText).isEmpty();
            if (noResults) {
                String screenshot = ScreenshotUtils.capture("no_results", keyword);
                logger.info("No results detected for keyword '{}' - screenshot saved at {}", keyword, screenshot);
                return List.of();
            }
        } catch (Exception e) {
            // ignore and proceed to try collecting results
        }

        List<WebElement> cards;
        try {
            cards = WaitUtils.waitForAllVisible(productCards, Duration.ofSeconds(25));
        } catch (Exception primaryFailure) {
            logger.warn("Primary product cards locator failed, attempting fallback locator", primaryFailure);
            // fallback to alternative locator
            cards = WaitUtils.waitForAllVisible(productCardsFallback, Duration.ofSeconds(15));
        }
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
