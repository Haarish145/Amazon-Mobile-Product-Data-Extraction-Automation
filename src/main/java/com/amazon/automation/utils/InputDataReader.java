package com.amazon.automation.utils;

import com.amazon.automation.model.ProductData;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class InputDataReader {
    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    private InputDataReader() {
    }

    public static List<String> readSearchKeywords(String inputFilePath) {
        try {
            Path path = Path.of(inputFilePath);
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("Input file does not exist: " + inputFilePath);
            }
            if (inputFilePath.endsWith(".csv")) {
                return readFromCsv(path);
            }
            if (inputFilePath.endsWith(".xlsx")) {
                return readFromExcel(path);
            }
            throw new IllegalArgumentException("Unsupported input format: " + inputFilePath);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to read input file", exception);
        }
    }

    private static List<String> readFromCsv(Path path) throws Exception {
        List<String> keywords = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(path.toFile()))) {
            List<String[]> rows = reader.readAll();
            if (rows.size() <= 1) {
                throw new IllegalArgumentException("Input CSV file is empty: " + path);
            }
            for (int index = 1; index < rows.size(); index++) {
                String[] row = rows.get(index);
                if (row.length > 0 && row[0] != null && !row[0].isBlank()) {
                    keywords.add(row[0].trim());
                }
            }
        }
        if (keywords.isEmpty()) {
            throw new IllegalArgumentException("No search keywords available in CSV file: " + path);
        }
        return keywords;
    }

    private static List<String> readFromExcel(Path path) throws Exception {
        List<String> keywords = new ArrayList<>();
        try (FileInputStream inputStream = new FileInputStream(path.toFile());
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                throw new IllegalArgumentException("Input Excel file is empty: " + path);
            }
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String value = DATA_FORMATTER.formatCellValue(cell);
                    if (!value.isBlank()) {
                        keywords.add(value.trim());
                    }
                }
            }
        }
        if (keywords.isEmpty()) {
            throw new IllegalArgumentException("No search keywords available in Excel file: " + path);
        }
        return keywords;
    }
}
