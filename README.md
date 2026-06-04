# Amazon Mobile Product Data Extraction Automation

A comprehensive Selenium WebDriver + TestNG automation framework for extracting mobile phone product information from Amazon. Includes CAPTCHA detection, resilient locator strategies, Excel/CSV support, comprehensive reporting, extensive unit tests, and CI/CD integration.

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Key Features](#key-features)
4. [Project Structure](#project-structure)
5. [Configuration](#configuration)
6. [Running Tests](#running-tests)
7. [Test Coverage](#test-coverage)
8. [Artifacts](#artifacts-generated)
9. [Setup Instructions](#setup-instructions)
10. [Troubleshooting](#troubleshooting)

## Project Overview

**Objective:** Automate Amazon mobile phone search, extract product details, save results to Excel/CSV, generate comprehensive reports, and maintain execution logs.

**Workflow:**
1. Read search keywords from CSV or XLSX
2. Launch Chrome browser and navigate to Amazon
3. Search for products using each keyword
4. Extract product data (name, price, rating, reviews, prime)
5. Handle CAPTCHA/no-results scenarios gracefully
6. Save results to CSV or XLSX
7. Generate Extent HTML report
8. Write detailed execution logs

## Technology Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Selenium WebDriver | 4.21.0 |
| TestNG | 7.10.2 |
| Maven | 3.13.0 |
| Apache POI | 5.2.5 |
| Extent Reports | 5.1.1 |
| Log4j | 2.23.1 |
| OpenCSV | 5.9 |
| Mockito (Unit Tests) | 5.2.1 |
| Commons IO | 2.16.1 |
| Commons Lang3 | 3.14.0 |

## Key Features

### 1. Robust CAPTCHA & No-Results Detection ✓
- Detects Amazon CAPTCHA ("Robot Check", "Enter characters..." etc.)
- Captures screenshots for diagnostics
- Logs detection with keyword context
- Returns gracefully (throws exception or empty results)

### 2. Keyword-Aware Screenshots ✓
- Screenshots include search keyword in filename (sanitized)
- Example: `no_results_mobile_phone_20260604_143022.png`
- Enables quick failure correlation to search conditions

### 3. Resilient Locator Strategy ✓
- **Primary:** `div[data-component-type='s-search-result']`
- **Fallback:** `div.s-result-item[data-asin]`
- Automatic failover with logging when primary fails

### 4. Multi-Format I/O (CSV & XLSX) ✓
- **Input:** Read keywords from CSV or XLSX
- **Output:** Write results to CSV or XLSX
- **Config-driven:** Preferences set in `config.properties`
- Automatic format detection based on file extension

### 5. Comprehensive Field Extraction ✓
- Product Name (skips if blank)
- Price (whole + fraction, N/A if missing)
- Rating (e.g., "4.5 out of 5 stars", N/A if missing)
- Reviews Count (N/A if missing)
- Prime Badge ("Yes" or "No")

### 6. Extent HTML Reporting ✓
- Beautiful, interactive test reports
- Test execution summary with pass/fail/skip counts
- System information and environment details
- Screenshot attachments for failures
- Execution timestamps and logs

### 7. Comprehensive Logging ✓
- Logs to: `logs/automation.log`
- Levels: INFO, WARN, ERROR, DEBUG
- Tracks: keywords, extractions, CAPTCHA, screenshots, timing

### 8. Git Integration & CI/CD ✓
- Full Git version control
- GitHub Actions CI workflow (runs unit tests on push/PR)
- Feature branch strategy (main / feature/automation)
- Automated test validation in CI

## Project Structure

```
Hackathon/
├── src/
│   ├── main/java/com/amazon/automation/
│   │   ├── config/            # Configuration management
│   │   │   └── FrameworkConfig.java
│   │   ├── constants/         # Framework constants
│   │   │   └── FrameworkConstants.java
│   │   ├── driver/            # WebDriver lifecycle management
│   │   │   └── DriverFactory.java
│   │   ├── exceptions/        # Custom exceptions
│   │   │   └── CaptchaDetectedException.java
│   │   ├── model/             # Data models
│   │   │   └── ProductData.java
│   │   ├── pages/             # Page objects
│   │   │   ├── AmazonHomePage.java
│   │   │   └── AmazonSearchResultsPage.java
│   │   ├── reporting/         # Extent Reports management
│   │   │   └── ExtentManager.java
│   │   └── utils/             # Utility classes
│   │       ├── InputDataReader.java
│   │       ├── OutputDataWriter.java
│   │       ├── ScreenshotUtils.java
│   │       └── WaitUtils.java
│   └── test/java/com/amazon/automation/
│       ├── base/              # Base test class
│       ├── pages/             # Page object unit tests
│       │   ├── AmazonHomePageTest.java
│       │   └── AmazonSearchResultsPageTest.java
│       ├── tests/             # Integration tests
│       │   ├── AmazonMobileExtractionTest.java
│       │   └── InputOutputExcelTest.java
│       ├── utils/             # Utility tests
│       │   ├── DataFileUtilityTest.java
│       │   └── LoggingTest.java
│       ├── driver/            # Driver tests
│       ├── reporting/         # Reporting tests
│       ├── git/               # Git repo tests
│       ├── listeners/         # Test listeners
│       └── resources/         # Config and test data
├── .github/workflows/
│   └── ci.yml                 # GitHub Actions CI pipeline
├── pom.xml                    # Maven POM
├── testng.xml                 # TestNG configuration
└── README.md                  # This file
```

## Configuration

### config.properties (src/test/resources/)
```properties
# Amazon URL
base.url=https://www.amazon.in/

# Browser settings
browser=chrome
headless=false

# Product extraction limit
max.products=10

# CSV input/output (defaults)
input.file=src/test/resources/testdata/input/search-keywords.csv
output.file=output/amazon-mobile-data.csv

# Excel input/output (optional, takes precedence if file exists)
input.xlsx.file=src/test/resources/testdata/input/search-keywords.xlsx
output.xlsx.file=output/amazon-mobile-data.xlsx
```

### log4j2.xml (src/test/resources/)
- Configured for console and file logging
- Default level: INFO
- Outputs to: `logs/automation.log`

### testng.xml (root)
- Defines test suite and listeners
- Includes all test classes
- Enables TestListener for reporting

## Running Tests

### Prerequisites
- Java 17+
- Maven 3.6+
- Chrome browser (or ChromeDriver for headless)
- Internet connection (for live Amazon tests)

### Build Project
```powershell
mvn clean install
```

### Run All Tests (Unit + Integration)
```powershell
mvn test
```

### Run Only Unit Tests (No Browser)
```powershell
mvn -Dtest="DataFileUtilityTest,AmazonSearchResultsPageTest,AmazonHomePageTest,DriverFactoryTest,ExtentManagerTest,LoggingTest,GitRepoTest,InputOutputExcelTest" test
```

### Run Specific Test Class
```powershell
mvn -Dtest=AmazonSearchResultsPageTest test
```

### Run Specific Test Method
```powershell
mvn -Dtest=AmazonSearchResultsPageTest#shouldThrowWhenCaptchaDetected test
```

### Run Headless (CI/CD Mode)
```powershell
mvn test -Dheadless=true
```

### Run with Custom Configuration
```powershell
mvn test -Dbase.url=https://www.amazon.com -Dmax.products=20
```

### Clean Build & Test
```powershell
mvn clean test
```

## Test Coverage

### Unit Tests (21 tests, ~5 minutes, no browser required)

| Test Class | Test Count | Focus |
|---|---|---|
| `DataFileUtilityTest` | 9 | CSV/XLSX read/write, validation, errors |
| `InputOutputExcelTest` | 1 | XLSX I/O flow |
| `AmazonSearchResultsPageTest` | 5 | CAPTCHA, no-results, fallback, parsing, missing fields |
| `AmazonHomePageTest` | 4 | Navigation, title, search, error propagation |
| `DriverFactoryTest` | 1 | Driver lifecycle |
| `ExtentManagerTest` | 1 | Report generation |
| `LoggingTest` | 1 | Log directory |
| `GitRepoTest` | 1 | Git presence |

**Total: 23 unit tests**

### Integration Tests (1 test, requires browser + internet)

| Test Class | Test Count | Focus |
|---|---|---|
| `AmazonMobileExtractionTest` | 1 | Full E2E: read → search → extract → export |

**Total: 1 integration test**

### Test Case Coverage Matrix

| ID | Scenario | Status | Type |
|---|---|---|---|
| TC01-TC05 | File validation, keyword read, browser launch, page open | ✓ | Unit + Integration |
| TC06-TC14 | Search, results, field extraction, data save | ✓ | Unit + Integration |
| TC15-TC17 | Report generation, logging, Git integration | ✓ | Unit |
| NTC01-NTC10 | Missing files, empty data, CAPTCHA, missing fields, locator changes | ✓ | Unit + Integration |

### All Test Cases Completed: 17 positive + 10 negative = **27 test scenarios covered**

## Artifacts Generated

After test execution, the following are created:

### Reports & Logs
- `reports/amazon-mobile-report.html` — Interactive Extent HTML report
- `logs/automation.log` — Detailed execution log (all levels)
- `screenshots/captcha_blocked_*.png` — CAPTCHA detection screenshots
- `screenshots/no_results_*.png` — No-results screenshots

### Data
- `output/amazon-mobile-data.csv` — Extracted data in CSV format
- `output/amazon-mobile-data.xlsx` — Extracted data in Excel format

## Setup Instructions

### 1. Clone Repository
```bash
git clone https://github.com/Haarish145/Amazon-Mobile-Product-Data-Extraction-Automation.git
cd Amazon-Mobile-Product-Data-Extraction-Automation
```

### 2. Checkout Development Branch
```bash
git checkout feature/automation
```

### 3. Verify Java Installation
```powershell
java -version   # Should show Java 17+
```

### 4. Build Project
```powershell
mvn clean install
```

### 5. Run Tests
```powershell
mvn test
```

### 6. View Report
Open `reports/amazon-mobile-report.html` in your web browser.

### 7. Check Logs
View execution details in `logs/automation.log`.

## Troubleshooting

### Build Fails: "Cannot resolve symbol 'mockito'"
**Solution:** Run `mvn clean install` to download all dependencies, or check internet connection.

### Test Fails: "ChromeDriver not found"
**Solution:** 
- Ensure Chrome browser is installed
- Use headless mode: `mvn test -Dheadless=true`
- Or install ChromeDriver matching your Chrome version

### Test Fails: "CAPTCHA detected"
**Solution:** 
- This is expected behavior — framework logs CAPTCHA and captures screenshot
- Live E2E tests may encounter CAPTCHA; unit tests mock this scenario
- Check `logs/automation.log` for screenshot location

### Test Fails: "Connection timeout"
**Solution:**
- Verify internet connection
- Check if Amazon site is accessible
- Try with a VPN (if needed in your region)
- Review `logs/automation.log` for network errors

### Test Fails: "Search keyword file not found"
**Solution:**
- Verify file path in `config.properties`
- Use relative path from project root
- Example: `src/test/resources/testdata/input/search-keywords.csv`

### Report Not Generated
**Solution:**
- Check `reports/` directory exists
- Verify write permissions on disk
- Review `logs/automation.log` for report generation errors

## CI/CD

### GitHub Actions Workflow
- **File:** `.github/workflows/ci.yml`
- **Trigger:** Push to main/feature/** branches and PRs to main
- **Job:** Run `mvn -B test` (all unit tests)
- **Status:** Check GitHub Actions tab for results

### Local CI Equivalent
```powershell
mvn -B clean test
```

## Future Enhancements

- Parallel test execution with TestNG
- Selenium Grid support for distributed testing
- Cloud browser integration (BrowserStack/Sauce Labs)
- Automatic retry mechanism for flaky tests
- Database storage for results
- Mobile browser testing
- Performance metrics and monitoring
- Direct screenshot embedding in Extent reports

## Contributing

1. Create feature branch: `git checkout -b feature/xyz`
2. Make changes and test locally
3. Commit with descriptive message: `git commit -m "feature: xyz"`
4. Push to GitHub: `git push origin feature/xyz`
5. Create Pull Request to `feature/automation` branch
6. Ensure CI passes before merge

## Support

- **Repository:** https://github.com/Haarish145/Amazon-Mobile-Product-Data-Extraction-Automation
- **Issues:** Use GitHub Issues for bug reports and feature requests
- **Documentation:** See README sections above

---

**Framework Version:** 1.0.0  
**Last Updated:** June 4, 2026  
**Status:** Complete with all test cases covered
