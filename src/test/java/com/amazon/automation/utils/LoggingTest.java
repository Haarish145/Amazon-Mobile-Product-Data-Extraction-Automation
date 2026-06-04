package com.amazon.automation.utils;

import com.amazon.automation.constants.FrameworkConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class LoggingTest {

    @Test
    public void logDirectoryShouldBeWritable() throws Exception {
        Path logDir = FrameworkConstants.PROJECT_ROOT.resolve(FrameworkConstants.LOG_DIRECTORY);
        Files.createDirectories(logDir);
        Path tmp = logDir.resolve("test-log.tmp");
        Files.writeString(tmp, "log test");
        Assert.assertTrue(Files.exists(tmp));
        Files.deleteIfExists(tmp);
    }
}

