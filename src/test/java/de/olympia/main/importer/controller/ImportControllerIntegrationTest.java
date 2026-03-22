package de.olympia.main.importer.controller;

import de.olympia.main.importer.dto.ImportResponseDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ImportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile createTestExcelFile() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("athleteFirstName");
        headerRow.createCell(1).setCellValue("athleteLastName");
        headerRow.createCell(2).setCellValue("sport");
        headerRow.createCell(3).setCellValue("rank");
        headerRow.createCell(4).setCellValue("timeOrPoints");
        headerRow.createCell(5).setCellValue("scoreType");
        headerRow.createCell(6).setCellValue("medal");

        // Create data row
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Mikaela");
        dataRow.createCell(1).setCellValue("Shiffrin");
        dataRow.createCell(2).setCellValue("Alpine Skiing");
        dataRow.createCell(3).setCellValue(1);
        dataRow.createCell(4).setCellValue("1:31.88");
        dataRow.createCell(5).setCellValue("TIME");
        dataRow.createCell(6).setCellValue("GOLD");

        workbook.write(baos);
        workbook.close();
        
        return new MockMultipartFile(
            "file",
            "test_results.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            baos.toByteArray()
        );
    }

    @Test
    void testImportResults_WithValidFile_ShouldReturnSuccess() throws Exception {
        MockMultipartFile file = createTestExcelFile();
        
        mockMvc.perform(multipart("/api/imports/results")
                .file(file)
                .param("userId", "1"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    }

    @Test
    void testImportResults_WithEmptyFile_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file",
            "empty.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            new byte[0]
        );
        
        mockMvc.perform(multipart("/api/imports/results")
                .file(emptyFile)
                .param("userId", "1"))
            .andExpect(status().isBadRequest())
            .andReturn();
    }

    @Test
    void testImportResults_WithInvalidFileType_ShouldReturnBadRequest() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "invalid content".getBytes()
        );
        
        mockMvc.perform(multipart("/api/imports/results")
                .file(invalidFile)
                .param("userId", "1"))
            .andExpect(status().isBadRequest())
            .andReturn();
    }
}

