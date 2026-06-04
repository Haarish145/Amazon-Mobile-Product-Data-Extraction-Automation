package com.amazon.automation.tests;

import com.amazon.automation.model.ProductData;
import com.amazon.automation.utils.InputDataReader;
import com.amazon.automation.utils.OutputDataWriter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class InputOutputExcelTest {

    @Test(description = "Validate reading keywords from XLSX and writing products to XLSX")
    public void excelReadWriteFlow() throws Exception {
        Path tempDir = Path.of("target", "test-excel");
        Files.createDirectories(tempDir);

        Path inputXlsx = tempDir.resolve("search-keywords.xlsx");
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Keywords");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("Keyword");
            var row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("mobile phone");
            var row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("charger");
            try (FileOutputStream os = new FileOutputStream(inputXlsx.toFile())) {
                workbook.write(os);
            }
        }

        List<String> keywords = InputDataReader.readSearchKeywords(inputXlsx.toString());
        Assert.assertEquals(keywords.size(), 2, "Expected two keywords from the generated Excel file");

        Path outputXlsx = tempDir.resolve("output-products.xlsx");
        var products = List.of(new ProductData("Sample Product", "199.99", "4.3 out of 5 stars", "123", "Yes"));
        OutputDataWriter.writeProducts(outputXlsx.toString(), products);
        Assert.assertTrue(Files.exists(outputXlsx), "Output XLSX file must be created");
    }
}

