package excelReader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {

    private Workbook workbook;

    public ExcelReader(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Excel file: " + filePath, e);
        }
    }

    /**
     * Get a cell value as Object (String, Integer, Double, Boolean, "")
     */
    public Object getCellValue(String sheetName, int rowNum, int colNum) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return null;
        Row row = sheet.getRow(rowNum);
        if (row == null) return null;
        Cell cell = row.getCell(colNum);
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                double num = cell.getNumericCellValue();
                if (num == (int) num) {
                    return (int) num;
                } else {
                    return num;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                switch (cellValue.getCellType()) {
                    case STRING:
                        return cellValue.getStringValue();
                    case NUMERIC:
                        double fnum = cellValue.getNumberValue();
                        if (fnum == (int) fnum) {
                            return (int) fnum;
                        } else {
                            return fnum;
                        }
                    case BOOLEAN:
                        return cellValue.getBooleanValue();
                    default:
                        return null;
                }
            case BLANK:
                return "";
            default:
                return null;
        }
    }

    /**
     * Total number of rows in a sheet (including header)
     */
    public int getRowCount(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) return 0;
        return sheet.getLastRowNum() + 1;
    }

    /**
     * Close workbook after use
     */
    public void close() {
        try {
            if (workbook != null) workbook.close();
        } catch (IOException e) {
            // Log or handle
        }
    }

    // Example usage
    public static void main(String[] args) {
        ExcelReader excel = new ExcelReader("src/main/resources/testdata.xlsx");
        Object value = excel.getCellValue("Sheet1", 0, 1);
        if (value != null) {
          System.out.println("Cell value: " + value + " (type: " + value.getClass().getSimpleName() + ")");
        }
        System.out.println("Total rows: " + excel.getRowCount("Sheet1"));
        excel.close();
    }
}
