package com.amazon.automation.reporting;

import com.amazon.automation.constants.FrameworkConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class ExtentManagerTest {

    @Test
    public void initializeReporterCreatesReportFile() throws Exception {
        ExtentManager.initializeReporter();
        Path reportPath = FrameworkConstants.PROJECT_ROOT.resolve(FrameworkConstants.REPORT_DIRECTORY).resolve("amazon-mobile-report.html");
        Assert.assertTrue(Files.exists(reportPath), "Extent report file should be created");
    }
}

