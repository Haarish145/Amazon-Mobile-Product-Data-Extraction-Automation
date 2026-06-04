# Test Execution Guide

This document provides detailed instructions for running all tests and understanding test coverage.

## Quick Start

### Run Everything
```powershell
mvn clean test
```

### Run Only Unit Tests (Recommended for Development)
```powershell
mvn -Dtest="DataFileUtilityTest,AmazonSearchResultsPageTest,AmazonHomePageTest,DriverFactoryTest,ExtentManagerTest,LoggingTest,GitRepoTest,InputOutputExcelTest" test
```

### Run Only Integration Tests (Requires Chrome & Internet)
```powershell
mvn -Dtest=AmazonMobileExtractionTest test
```

## Test Categories

### 1. Unit Tests (No Browser Required - ~5 minutes)

These tests do NOT require a browser and can run in CI/CD without external dependencies.

#### Input/Output Tests
```powershell
mvn -Dtest=DataFileUtilityTest test
```
**Covers:**
- CSV file reading and validation
- Excel (XLSX) file reading and validation
- Empty/missing file error handling
- CSV output writing
- Excel output writing
- Unsupported format error handling
- Successful keyword reading from both formats

**Test Methods:**
- shouldThrowWhenInputCsvIsMissing
- shouldThrowWhenInputCsvIsEmpty
- shouldThrowWhenInputExcelHasNoKeywords
- shouldWriteProductsToCsv
- shouldWriteProductsToExcel
- shouldThrowWhenOutputExtensionIsUnsupported
- shouldReadKeywordsFromCsvSuccessfully
- shouldThrowWhenInputXlsxMissing

#### Excel I/O Test
```powershell
mvn -Dtest=InputOutputExcelTest test
```
**Covers:**
- Programmatic XLSX file creation
- XLSX keyword reading
- XLSX product output writing
- XLSX file existence verification

#### Page Object Tests (With Mocking)
```powershell
mvn -Dtest=AmazonHomePageTest test
```
**Covers:**
- Browser navigation (mocked)
- Title retrieval
- Search box interaction
- Error propagation (network failures)

**Test Methods:**
- openShouldNavigateToBaseUrlAndWaitForSearchBox
- getTitleReturnsDriverTitle
- searchForShouldPopulateAndSubmit
- openShouldPropagateDriverExceptionsWhenInternetUnavailable

```powershell
mvn -Dtest=AmazonSearchResultsPageTest test
```
**Covers:**
- CAPTCHA detection and screenshot capture
- No-results detection and screenshot capture
- Fallback locator usage
- Product field extraction (name, price, rating, reviews, prime)
- Missing field handling (all defaulting to N/A or "No")

**Test Methods:**
- shouldThrowWhenCaptchaDetected
- shouldReturnEmptyWhenNoResultsDetected
- shouldUseFallbackLocatorAndParseProduct
- shouldHandleMissingPriceAndSetNA
- shouldHandleMissingRatingReviewsAndPrimeBadge

#### Driver Lifecycle Test
```powershell
mvn -Dtest=DriverFactoryTest test
```
**Covers:**
- Driver not initialized error

#### Reporting Test
```powershell
mvn -Dtest=ExtentManagerTest test
```
**Covers:**
- Extent reporter initialization
- HTML report file creation

#### Utility Tests
```powershell
mvn -Dtest=LoggingTest test
```
**Covers:**
- Log directory creation and writability

```powershell
mvn -Dtest=GitRepoTest test
```
**Covers:**
- Git repository presence in project

### 2. Integration Tests (Requires Browser & Internet - ~2-5 minutes)

These tests require Chrome browser and internet connection to Amazon. They perform live Selenium automation.

```powershell
mvn -Dtest=AmazonMobileExtractionTest test
```

**Full E2E Flow:**
1. Reads search keywords from configured input file (CSV or XLSX)
2. Opens Chrome and navigates to Amazon
3. For each keyword:
   - Searches for the keyword
   - Extracts products (up to max.products limit)
   - Collects product data
4. Writes all results to output file (CSV or XLSX)
5. Generates Extent report
6. Writes execution logs

**What It Tests:**
- File I/O in real scenario
- Browser automation (real)
- Amazon website accessibility
- Product data extraction accuracy
- Report generation
- Log creation

## Running Specific Test Scenarios

### Test CAPTCHA Detection Only (Unit)
```powershell
mvn -Dtest=AmazonSearchResultsPageTest#shouldThrowWhenCaptchaDetected test
```

### Test No-Results Handling Only (Unit)
```powershell
mvn -Dtest=AmazonSearchResultsPageTest#shouldReturnEmptyWhenNoResultsDetected test
```

### Test CSV File Operations Only
```powershell
mvn -Dtest=DataFileUtilityTest#shouldWriteProductsToCsv,DataFileUtilityTest#shouldReadKeywordsFromCsvSuccessfully test
```

### Test Excel File Operations Only
```powershell
mvn -Dtest=DataFileUtilityTest#shouldWriteProductsToExcel,InputOutputExcelTest test
```

### Test Full E2E Flow
```powershell
mvn -Dtest=AmazonMobileExtractionTest test
```

## Running Tests with Custom Configuration

### Test with Different Amazon Site
```powershell
mvn test -Dbase.url=https://www.amazon.com
```

### Test with Custom Product Limit
```powershell
mvn test -Dmax.products=20
```

### Test in Headless Mode (No Browser UI)
```powershell
mvn test -Dheadless=true
```

### Test with Verbose Logging
Set log level in `src/test/resources/log4j2.xml`:
```xml
<Root level="DEBUG">
```
Then run: `mvn test`

## Test Execution Matrix

| Category | Test Class | Tests | Browser | Time | CI/CD |
|---|---|---|---|---|---|
| Input/Output | DataFileUtilityTest | 9 | ✗ | <1m | ✓ |
| Excel I/O | InputOutputExcelTest | 1 | ✗ | <1m | ✓ |
| HomePage | AmazonHomePageTest | 4 | ✗ Mocked | <1m | ✓ |
| Search Results | AmazonSearchResultsPageTest | 5 | ✗ Mocked | <1m | ✓ |
| Driver | DriverFactoryTest | 1 | ✗ | <1m | ✓ |
| Reporting | ExtentManagerTest | 1 | ✗ | <1m | ✓ |
| Logging | LoggingTest | 1 | ✗ | <1m | ✓ |
| Git Repo | GitRepoTest | 1 | ✗ | <1m | ✓ |
| **Unit Total** | **8 classes** | **23** | **✗** | **~5m** | **✓** |
| **E2E** | **AmazonMobileExtractionTest** | **1** | **✓** | **2-5m** | **✗** |
| **Total** | **9 classes** | **24** | **Mixed** | **~7-10m** | **Variable** |

## Test Results Interpretation

### Successful Unit Test Run
```
[INFO] BUILD SUCCESS
[INFO] Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
```

### Successful Integration Test Run  
```
[INFO] BUILD SUCCESS
[INFO] Tests run: 24, Failures: 0, Errors: 0, Skipped: 0
```

### Expected Artifacts After Run
```
reports/amazon-mobile-report.html          (Extent HTML report)
logs/automation.log                          (Execution logs)
output/amazon-mobile-data.csv               (Extracted data - CSV)
output/amazon-mobile-data.xlsx              (Extracted data - Excel)
screenshots/captcha_blocked_*.png           (CAPTCHA screenshots)
screenshots/no_results_*.png                (No-results screenshots)
```

## Troubleshooting Test Failures

### "Test execution failed with code 1"
Check `logs/automation.log` for detailed error messages.

### "NoSuchElementException: cannot find element"
- Unit tests: This shouldn't happen (mocking). Report as a bug.
- Integration tests: Amazon page layout changed. Update locators in `AmazonSearchResultsPage.java`.

### "Connection timeout"
- Check internet connection
- Verify Amazon.in is accessible
- Unit tests shouldn't have this issue

### "CAPTCHA detected"
- Expected in live E2E tests
- Check logs and screenshot location
- May require re-running after timeout

### All unit tests pass but E2E fails
- Browser/internet issue (check logs)
- Amazon CSS selectors changed (update page objects)
- Try running test again (transient failures possible)

## CI/CD Test Execution

GitHub Actions automatically runs unit tests on every push and PR:

```yaml
# .github/workflows/ci.yml
- name: Run unit tests
  run: mvn -B test
```

CI only runs unit tests (no browser required) for faster feedback.

## Best Practices

1. **Local Development:** Run unit tests frequently for fast feedback
   ```powershell
   mvn test -Dtest="DataFileUtilityTest,AmazonSearchResultsPageTest"
   ```

2. **Before Commit:** Run full unit suite to ensure nothing broke
   ```powershell
   mvn test -Dtest="*Test"
   ```

3. **Full Validation:** Run integration tests locally before pushing
   ```powershell
   mvn test
   ```

4. **In CI/CD:** Automatically runs unit tests on PR (no manual action needed)

5. **After Merge:** Maintain test coverage above 80%

## Performance Tips

1. **Skip integration tests during development:**
   ```powershell
   mvn test -Dtest="*Test" -Dskip-integration=true
   ```
   (Note: Use `-DexcludedGroups=integration` if TestNG groups are configured)

2. **Run tests in parallel (future enhancement):**
   ```powershell
   mvn test -Dsuites=testng.xml -Dparallel=methods -Dthreadcount=4
   ```

3. **Use headless mode for faster E2E (if needed):**
   ```powershell
   mvn test -Dheadless=true
   ```

## Questions?

- Check `logs/automation.log` first (most detailed info)
- Review test class JavaDoc comments
- Check GitHub Issues for similar problems
- Refer to main `README.md` for framework overview

---

**Version:** 1.0.0  
**Last Updated:** June 4, 2026

