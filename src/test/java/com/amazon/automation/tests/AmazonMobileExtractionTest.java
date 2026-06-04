package com.amazon.automation.tests;

import com.amazon.automation.base.BaseTest;
import com.amazon.automation.config.FrameworkConfig;
import com.amazon.automation.constants.FrameworkConstants;
import com.amazon.automation.driver.DriverFactory;
import com.amazon.automation.model.ProductData;
import com.amazon.automation.pages.AmazonHomePage;
import com.amazon.automation.pages.AmazonSearchResultsPage;
import com.amazon.automation.reporting.ExtentManager;
import com.amazon.automation.utils.InputDataReader;
import com.amazon.automation.utils.OutputDataWriter;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

public class AmazonMobileExtractionTest extends BaseTest {

    @Test(description = "Read search keywords, search Amazon mobile phones, and export product data")
    public void extractMobileProductData() {
        // Prefer XLSX input if configured and file exists; otherwise fall back to CSV
        String configuredXlsx = FrameworkConfig.get("input.xlsx.file");
        String configuredCsv = FrameworkConfig.getOrDefault("input.file", FrameworkConstants.DEFAULT_INPUT_FILE);
        String inputFile;
        if (configuredXlsx != null && !configuredXlsx.isBlank() && Files.exists(Path.of(configuredXlsx))) {
            inputFile = configuredXlsx;
        } else {
            inputFile = configuredCsv;
        }

        // Prefer XLSX output if configured, else default CSV output
        String outputFile = FrameworkConfig.getOrDefault("output.xlsx.file", FrameworkConfig.getOrDefault("output.file", FrameworkConstants.DEFAULT_OUTPUT_FILE));
        int maxProducts = FrameworkConfig.getInt("max.products", 10);

        List<String> keywords = InputDataReader.readSearchKeywords(inputFile);
        Assert.assertFalse(keywords.isEmpty(), "Search keyword list must not be empty");

        AmazonHomePage homePage = new AmazonHomePage(DriverFactory.getDriver());
        AmazonSearchResultsPage resultsPage = new AmazonSearchResultsPage(DriverFactory.getDriver());
        List<ProductData> allProducts = new ArrayList<>();

        for (String keyword : keywords) {
            logger.info("Executing search for keyword: {}", keyword);
            ExtentManager.getTest().info("Executing search for keyword: " + keyword);

            homePage.open(FrameworkConfig.getOrDefault("base.url", FrameworkConstants.DEFAULT_BASE_URL));
            Assert.assertTrue(homePage.getTitle().toLowerCase().contains("amazon"), "Amazon page title validation failed");

            homePage.searchFor(keyword);
            List<ProductData> products = resultsPage.extractProducts(keyword, maxProducts);
            logger.info("Extracted {} products for keyword: {}", products.size(), keyword);
            ExtentManager.getTest().info("Extracted " + products.size() + " products for keyword: " + keyword);
            allProducts.addAll(products);
        }

        OutputDataWriter.writeProducts(outputFile, allProducts);
        logger.info("Output file generated at {}", outputFile);
        ExtentManager.getTest().pass("Product data saved to: " + outputFile);
        Assert.assertFalse(allProducts.isEmpty(), "No products were extracted for the configured keywords");
    }
}
