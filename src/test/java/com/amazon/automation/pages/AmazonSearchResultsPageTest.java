package com.amazon.automation.pages;

import com.amazon.automation.driver.DriverFactory;
import com.amazon.automation.exceptions.CaptchaDetectedException;
import com.amazon.automation.utils.WaitUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AmazonSearchResultsPageTest {

    private WebDriver mockDriver;

    @BeforeMethod
    public void setUp() throws Exception {
        mockDriver = mock(WebDriver.class, withSettings().extraInterfaces(TakesScreenshot.class));
        // inject mock driver into DriverFactory's ThreadLocal via reflection
        Field field = DriverFactory.class.getDeclaredField("DRIVER");
        field.setAccessible(true);
        ThreadLocal<WebDriver> tl = (ThreadLocal<WebDriver>) field.get(null);
        tl.set(mockDriver);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        // clear ThreadLocal
        Field field = DriverFactory.class.getDeclaredField("DRIVER");
        field.setAccessible(true);
        ThreadLocal<WebDriver> tl = (ThreadLocal<WebDriver>) field.get(null);
        tl.remove();
    }

    @Test
    public void shouldThrowWhenCaptchaDetected() {
        when(mockDriver.getTitle()).thenReturn("Robot Check - Amazon");
        when(mockDriver.getPageSource()).thenReturn("Enter the characters you see below");
        // make screenshot call safe
        TakesScreenshot ts = (TakesScreenshot) mockDriver;
        File tmp = new File(System.getProperty("java.io.tmpdir"), "screenshot.png");
        when(ts.getScreenshotAs(OutputType.FILE)).thenReturn(tmp);

        AmazonSearchResultsPage page = new AmazonSearchResultsPage(mockDriver);
        Assert.expectThrows(CaptchaDetectedException.class, () -> page.extractProducts("mobile", 5));
    }

    @Test
    public void shouldReturnEmptyWhenNoResultsDetected() {
        when(mockDriver.getTitle()).thenReturn("Amazon.in");
        when(mockDriver.getPageSource()).thenReturn("normal page");
        // driver.findElements should return non-empty for no-results locator check
        when(mockDriver.findElements(any(By.class))).thenReturn(List.of(mock(WebElement.class)));
        TakesScreenshot ts = (TakesScreenshot) mockDriver;
        File tmp = new File(System.getProperty("java.io.tmpdir"), "screenshot2.png");
        when(ts.getScreenshotAs(OutputType.FILE)).thenReturn(tmp);

        AmazonSearchResultsPage page = new AmazonSearchResultsPage(mockDriver);
        List<?> result = page.extractProducts("mobile", 5);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void shouldUseFallbackLocatorAndParseProduct() throws Exception {
        when(mockDriver.getTitle()).thenReturn("Amazon.in");
        when(mockDriver.getPageSource()).thenReturn("normal page");
        // ensure no-results checks return empty
        when(mockDriver.findElements(any(By.class))).thenReturn(List.of());

        // prepare a mock card element with nested elements
        WebElement mockCard = mock(WebElement.class);
        WebElement nameEl = mock(WebElement.class);
        WebElement wholeEl = mock(WebElement.class);
        WebElement fractionEl = mock(WebElement.class);
        WebElement ratingEl = mock(WebElement.class);
        WebElement reviewsEl = mock(WebElement.class);

        when(nameEl.getText()).thenReturn("Sample Phone");
        when(wholeEl.getText()).thenReturn("19999");
        when(fractionEl.getText()).thenReturn("00");
        when(ratingEl.getText()).thenReturn("4.5 out of 5 stars");
        when(reviewsEl.getText()).thenReturn("123 reviews");

        // Answer to return appropriate child based on locator string
        when(mockCard.findElement(any(By.class))).thenAnswer(invocation -> {
            By by = invocation.getArgument(0);
            String locator = by.toString();
            if (locator.contains("h2") || locator.contains("h2 span")) return nameEl;
            if (locator.contains("a-price-whole")) return wholeEl;
            if (locator.contains("a-price-fraction")) return fractionEl;
            if (locator.contains("a-icon-alt")) return ratingEl;
            if (locator.toString().contains("ratings") || locator.contains("a-row.a-size-small")) return reviewsEl;
            throw new NoSuchElementException("not found: " + locator);
        });

        List<WebElement> fallbackList = List.of(mockCard);

        // Mock static WaitUtils so primary call throws and fallback returns our list
        try (MockedStatic<WaitUtils> mocked = Mockito.mockStatic(WaitUtils.class)) {
            mocked.when(() -> WaitUtils.waitForAllVisible(any(By.class), any(Duration.class)))
                    .thenThrow(new RuntimeException("primary failed")).thenReturn(fallbackList);

            AmazonSearchResultsPage page = new AmazonSearchResultsPage(mockDriver);
            List<?> products = page.extractProducts("mobile", 5);
            Assert.assertFalse(products.isEmpty());
        }
    }
}

