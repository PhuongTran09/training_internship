package com.example.backend.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportReport(@RequestParam String format) {
        try {
            byte[] data = reportService.exportReport(format);
            HttpHeaders headers = new HttpHeaders();
            if (format.equalsIgnoreCase("pdf")) {
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf");
                headers.setContentType(MediaType.APPLICATION_PDF);
            } else if (format.equalsIgnoreCase("xlsx")) {
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.xlsx");
                headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

            }
            
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
