package com.amazon.automation.utils;

import com.amazon.automation.constants.FrameworkConstants;
import com.amazon.automation.driver.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ScreenshotUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ScreenshotUtils() {
    }

    public static String capture(String filePrefix) {
        return capture(filePrefix, null);
    }

    /**
     * Capture a screenshot with an optional context (e.g. search keyword) appended to the filename.
     * The context will be sanitized to be filesystem-safe.
     */
    public static String capture(String filePrefix, String context) {
        try {
            Path screenshotDirectory = FrameworkConstants.PROJECT_ROOT.resolve(FrameworkConstants.SCREENSHOT_DIRECTORY);
            Files.createDirectories(screenshotDirectory);
            String safeContext = "";
            if (context != null && !context.isBlank()) {
                // sanitize context for file names: allow alphanumeric, dash, underscore and dot; replace others with '_'
                safeContext = "_" + context.replaceAll("[^A-Za-z0-9._-]", "_");
                if (safeContext.length() > 60) {
                    safeContext = safeContext.substring(0, 60);
                }
            }
            File source = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.FILE);
            Path target = screenshotDirectory.resolve(filePrefix + safeContext + "_" + FORMATTER.format(LocalDateTime.now()) + ".png");
            FileUtils.copyFile(source, target.toFile());
            return target.toString();
        } catch (Exception exception) {
            return "Screenshot capture failed: " + exception.getMessage();
        }
    }


