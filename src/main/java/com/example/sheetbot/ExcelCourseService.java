package com.example.sheetbot;

import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelCourseService {

    @Value("${sheet.excel.url}")
    private String excelUrl;

    private final List<Course> courseList = new ArrayList<>();

    public List<Course> getAllCourses() {
        return courseList;
    }

    @PostConstruct
    public void loadExcelData() {
        try (InputStream inputStream = new URL(excelUrl).openStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            // Read Sheet 1 (LMS)
            Sheet sheet1 = workbook.getSheetAt(0);
            Row header1 = sheet1.getRow(0);
            String lmsUrl = "https://corporatelearning.playablo.com/cl/player/login";

            for (int r = 1; r <= sheet1.getLastRowNum(); r++) {
                Row row = sheet1.getRow(r);
                if (row == null) continue;
                for (int c = 0; c < row.getLastCellNum(); c++) {
                    Cell cell = row.getCell(c);
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        String courseName = cell.getStringCellValue().trim();
                        if (!courseName.isEmpty()) {
                            courseList.add(new Course(courseName, "LMS", lmsUrl));
                        }
                    }
                }
            }

            // Read Sheet 2 (LinkedIn)
            Sheet sheet2 = workbook.getSheetAt(1);
            int titleIndex = -1, urlIndex = -1;
            Row header2 = sheet2.getRow(0);
            for (Cell cell : header2) {
                if (cell.getStringCellValue().equalsIgnoreCase("Course Title")) titleIndex = cell.getColumnIndex();
                if (cell.getStringCellValue().equalsIgnoreCase("Course URL")) urlIndex = cell.getColumnIndex();
            }

            for (int r = 1; r <= sheet2.getLastRowNum(); r++) {
                Row row = sheet2.getRow(r);
                if (row == null) continue;
                Cell nameCell = row.getCell(titleIndex);
                Cell linkCell = row.getCell(urlIndex);
                if (nameCell != null && linkCell != null) {
                    String name = nameCell.getStringCellValue().trim();
                    String link = linkCell.getStringCellValue().trim();
                    if (!name.isEmpty() && !link.isEmpty()) {
                        courseList.add(new Course(name, "LinkedIn", link));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
