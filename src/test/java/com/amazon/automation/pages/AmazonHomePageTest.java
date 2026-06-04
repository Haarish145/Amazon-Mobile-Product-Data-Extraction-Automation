package com.amazon.automation.pages;

import com.amazon.automation.driver.DriverFactory;
import com.amazon.automation.utils.WaitUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AmazonHomePageTest {

    private WebDriver mockDriver;

    @BeforeMethod
    public void setUp() throws Exception {
        mockDriver = mock(WebDriver.class);
        // inject into DriverFactory ThreadLocal
        Field field = DriverFactory.class.getDeclaredField("DRIVER");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<WebDriver> tl = (ThreadLocal<WebDriver>) field.get(null);
        tl.set(mockDriver);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        Field field = DriverFactory.class.getDeclaredField("DRIVER");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        ThreadLocal<WebDriver> tl = (ThreadLocal<WebDriver>) field.get(null);
        tl.remove();
    }

    @Test
    public void openShouldNavigateToBaseUrlAndWaitForSearchBox() {
        AmazonHomePage page = new AmazonHomePage(mockDriver);
        try (MockedStatic<WaitUtils> mocked = Mockito.mockStatic(WaitUtils.class)) {
            WebElement mockEl = mock(WebElement.class);
            mocked.when(() -> WaitUtils.waitForVisible(any(By.class), any(Duration.class))).thenReturn(mockEl);

            String url = "https://www.amazon.in/";
            page.open(url);
            verify(mockDriver, times(1)).get(url);
        }
    }

    @Test
    public void getTitleReturnsDriverTitle() {
        when(mockDriver.getTitle()).thenReturn("Amazon.in");
        AmazonHomePage page = new AmazonHomePage(mockDriver);
        Assert.assertEquals(page.getTitle(), "Amazon.in");
    }

    @Test
    public void searchForShouldPopulateAndSubmit() {
        AmazonHomePage page = new AmazonHomePage(mockDriver);
        WebElement mockSearch = mock(WebElement.class);
        WebElement mockButton = mock(WebElement.class);
        try (MockedStatic<WaitUtils> mocked = Mockito.mockStatic(WaitUtils.class)) {
            // First two calls for searchBox, third for searchButton
            mocked.when(() -> WaitUtils.waitForVisible(any(By.class), any(Duration.class)))
                    .thenReturn(mockSearch).thenReturn(mockSearch).thenReturn(mockButton);

            page.searchFor("mobile");
            verify(mockSearch, times(1)).clear();
            verify(mockSearch, times(1)).sendKeys("mobile");
            verify(mockButton, times(1)).click();
        }
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void openShouldPropagateDriverExceptionsWhenInternetUnavailable() {
        doThrow(new RuntimeException("network error")).when(mockDriver).get(anyString());
        AmazonHomePage page = new AmazonHomePage(mockDriver);
        page.open("https://www.amazon.in/");
    }
}

