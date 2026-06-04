package com.amazon.automation.reporting;

import com.amazon.automation.constants.FrameworkConstants;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ExtentManager {
    private static final ExtentReports EXTENT_REPORTS = new ExtentReports();
    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();
    private static boolean initialized;

    private ExtentManager() {
    }

    public static synchronized void initializeReporter() {
        if (initialized) {
            return;
        }
        try {
            Path reportDirectory = FrameworkConstants.PROJECT_ROOT.resolve(FrameworkConstants.REPORT_DIRECTORY);
            Files.createDirectories(reportDirectory);
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDirectory.resolve("amazon-mobile-report.html").toString());
            sparkReporter.config().setDocumentTitle("Amazon Mobile Data Extraction");
            sparkReporter.config().setReportName("Automation Execution Report");
            EXTENT_REPORTS.attachReporter(sparkReporter);
            EXTENT_REPORTS.setSystemInfo("Project", "Amazon Mobile Data Extraction Automation");
            EXTENT_REPORTS.setSystemInfo("Framework", "Selenium + TestNG + Maven");
            initialized = true;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to initialize extent report", exception);
        }
    }

    public static void createTest(String testName) {
        EXTENT_TEST.set(EXTENT_REPORTS.createTest(testName));
    }

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    public static synchronized void flushReport() {
        EXTENT_REPORTS.flush();
    }
}
