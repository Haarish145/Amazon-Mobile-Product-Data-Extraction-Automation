package com.amazon.automation.config;

import com.amazon.automation.constants.FrameworkConstants;

import java.io.InputStream;
import java.util.Properties;

public final class FrameworkConfig {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = FrameworkConfig.class.getClassLoader()
                .getResourceAsStream(FrameworkConstants.CONFIG_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Unable to locate config.properties on the classpath");
            }
            PROPERTIES.load(inputStream);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private FrameworkConfig() {
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = PROPERTIES.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : Integer.parseInt(value.trim());
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = PROPERTIES.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : Boolean.parseBoolean(value.trim());
    }
}
