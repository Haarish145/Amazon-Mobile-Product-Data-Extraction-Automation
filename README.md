# Amazon Mobile Data Extraction Automation

Java Selenium-TestNG framework to read search keywords from CSV/XLSX, search Amazon mobile products, extract key product data, export results, generate reports, and maintain logs.

## Stack
- Java 17
- Selenium WebDriver
- TestNG
- Maven
- Apache POI
- Extent Reports
- Log4j2
- Git

## Project Structure
- `src/main/java/com/amazon/automation` framework code
- `src/test/java/com/amazon/automation/tests` TestNG test cases
- `src/test/resources/config.properties` execution settings
- `src/test/resources/testdata/input/search-keywords.csv` sample input file
- `testng.xml` TestNG suite entry point

## Run
```bash
./mvnw clean test
```

On Windows:
```powershell
.\mvnw.cmd clean test
```

## Workflow Coverage
1. Reads search keywords from CSV or XLSX.
2. Launches Chrome and opens Amazon.
3. Searches each keyword.
4. Extracts product name, price, rating, reviews count, and Prime badge.
5. Saves results to CSV or XLSX.
6. Generates Extent report in `reports/`.
7. Writes execution logs in `logs/`.

## Notes
- CAPTCHA or access restrictions are surfaced as execution failures.
- Missing price, rating, and reviews are written as `N/A`.
- Missing Prime badge is written as `No`.
