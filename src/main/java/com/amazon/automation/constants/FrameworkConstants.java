package com.amazon.automation.constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class FrameworkConstants {
    public static final String CONFIG_FILE = "config.properties";
    public static final String LOG4J2_FILE = "log4j2.xml";
    public static final String DEFAULT_BASE_URL = "https://www.amazon.in/";
    public static final String DEFAULT_BROWSER = "chrome";
    public static final String DEFAULT_INPUT_FILE = "src/test/resources/testdata/input/search-keywords.csv";
    public static final String DEFAULT_OUTPUT_FILE = "output/amazon-mobile-data.csv";
    public static final String REPORT_DIRECTORY = "reports";
    public static final String SCREENSHOT_DIRECTORY = "screenshots";
    public static final String LOG_DIRECTORY = "logs";
    public static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir"));

    private FrameworkConstants() {
    }
}
