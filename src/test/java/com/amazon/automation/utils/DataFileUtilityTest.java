package com.amazon.automation.utils;

import com.amazon.automation.model.ProductData;
import com.opencsv.CSVReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DataFileUtilityTest {

    @Test
    public void shouldThrowWhenInputCsvIsMissing() {
        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class,
                () -> InputDataReader.readSearchKeywords("src/test/resources/testdata/input/missing.csv"));

        Assert.assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    public void shouldThrowWhenInputCsvIsEmpty() throws Exception {
        Path tempFile = Files.createTempFile("empty-keywords", ".csv");
        Files.writeString(tempFile, "SearchKeyword\n");

        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class,
                () -> InputDataReader.readSearchKeywords(tempFile.toString()));

        Assert.assertTrue(exception.getMessage().contains("empty"));
    }

    @Test
    public void shouldThrowWhenInputExcelHasNoKeywords() throws Exception {
        Path tempFile = Files.createTempFile("empty-keywords", ".xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Input");
            sheet.createRow(0).createCell(0).setCellValue("SearchKeyword");
            try (var outputStream = Files.newOutputStream(tempFile)) {
                workbook.write(outputStream);
            }
        }

        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class,
                () -> InputDataReader.readSearchKeywords(tempFile.toString()));

        Assert.assertTrue(exception.getMessage().contains("empty"));
    }

    @Test
    public void shouldWriteProductsToCsv() throws Exception {
        Path tempFile = Files.createTempFile("amazon-output", ".csv");
        List<ProductData> products = List.of(new ProductData("Phone A", "14999.00", "4.2", "120", "Yes"));

        OutputDataWriter.writeProducts(tempFile.toString(), products);

        try (CSVReader reader = new CSVReader(new FileReader(tempFile.toFile()))) {
            List<String[]> rows = reader.readAll();
            Assert.assertEquals(rows.get(0), new String[]{"Product Name", "Price", "Rating", "Reviews Count", "Prime Badge"});
            Assert.assertEquals(rows.get(1), new String[]{"Phone A", "14999.00", "4.2", "120", "Yes"});
        }
    }

    @Test
    public void shouldWriteProductsToExcel() throws Exception {
        Path tempFile = Files.createTempFile("amazon-output", ".xlsx");
        List<ProductData> products = List.of(new ProductData("Phone B", "21999.00", "4.4", "245", "No"));

        OutputDataWriter.writeProducts(tempFile.toString(), products);

        try (FileInputStream inputStream = new FileInputStream(tempFile.toFile());
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            Assert.assertEquals(workbook.getSheetAt(0).getRow(1).getCell(0).getStringCellValue(), "Phone B");
            Assert.assertEquals(workbook.getSheetAt(0).getRow(1).getCell(4).getStringCellValue(), "No");
        }
    }

    @Test
    public void shouldThrowWhenOutputExtensionIsUnsupported() {
        IllegalArgumentException exception = Assert.expectThrows(IllegalArgumentException.class,
                () -> OutputDataWriter.writeProducts("output/amazon-mobile-data.txt", List.of()));

        Assert.assertTrue(exception.getMessage().contains("Unsupported output format"));
    }
}
