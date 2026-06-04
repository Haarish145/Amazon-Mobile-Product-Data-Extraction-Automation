package com.amazon.automation.driver;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DriverFactoryTest {

    @Test
    public void getDriverShouldThrowWhenNotInitialized() {
        // Ensure driver is not initialized
        DriverFactory.quitDriver();
        try {
            DriverFactory.getDriver();
            Assert.fail("Expected IllegalStateException when driver is not initialized");
        } catch (IllegalStateException expected) {
            // pass
        }
    }
}

