package com.amazon.automation.utils;

import com.amazon.automation.model.ProductData;
import com.opencsv.CSVWriter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class OutputDataWriter {
    private static final String[] HEADERS = {"Product Name", "Price", "Rating", "Reviews Count", "Prime Badge"};

    private OutputDataWriter() {
    }

    public static void writeProducts(String outputFilePath, List<ProductData> products) {
        try {
            if (outputFilePath == null || outputFilePath.isBlank()) {
                throw new IllegalArgumentException("Output file path must not be blank");
            }
            if (products == null) {
                throw new IllegalArgumentException("Products list must not be null");
            }
            Path outputPath = Path.of(outputFilePath);
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (outputFilePath.endsWith(".csv")) {
                writeCsv(outputPath, products);
                return;
            }
            if (outputFilePath.endsWith(".xlsx")) {
                writeExcel(outputPath, products);
                return;
            }
            throw new IllegalArgumentException("Unsupported output format: " + outputFilePath);
        } catch (IllegalArgumentException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to write output file", exception);
        }
    }

    private static void writeCsv(Path outputPath, List<ProductData> products) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath.toFile()))) {
            writer.writeNext(HEADERS);
            for (ProductData product : products) {
                writer.writeNext(new String[]{
                        product.getProductName(),
                        product.getPrice(),
                        product.getRating(),
                        product.getReviewsCount(),
                        product.getPrimeBadge()
                });
            }
        }
    }

    private static void writeExcel(Path outputPath, List<ProductData> products) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Amazon Mobile Data");
            var headerRow = sheet.createRow(0);
            for (int index = 0; index < HEADERS.length; index++) {
                headerRow.createCell(index).setCellValue(HEADERS[index]);
            }
            int rowIndex = 1;
            for (ProductData product : products) {
                var row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(product.getProductName());
                row.createCell(1).setCellValue(product.getPrice());
                row.createCell(2).setCellValue(product.getRating());
                row.createCell(3).setCellValue(product.getReviewsCount());
                row.createCell(4).setCellValue(product.getPrimeBadge());
            }
            for (int index = 0; index < HEADERS.length; index++) {
                sheet.autoSizeColumn(index);
            }
            try (FileOutputStream outputStream = new FileOutputStream(outputPath.toFile())) {
                workbook.write(outputStream);
            }
        }
    }
}
