package com.amazon.automation.base;

import com.amazon.automation.config.FrameworkConfig;
import com.amazon.automation.driver.DriverFactory;
import com.amazon.automation.reporting.ExtentManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public abstract class BaseTest {
    protected final Logger logger = LogManager.getLogger(getClass());

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        ExtentManager.initializeReporter();
        logger.info("Suite initialization completed");
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initializeDriver();
        logger.info("Browser launched: {}", FrameworkConfig.getOrDefault("browser", "chrome"));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
        logger.info("Browser session closed");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        ExtentManager.flushReport();
        logger.info("Extent report flushed");
    }
}
