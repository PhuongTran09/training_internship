package com.example.backend.service.report;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private DataSource reportDataSource;

    @SuppressWarnings("UseSpecificCatch")
    public byte[] exportReport(String format) throws Exception {
        Connection connection = null;
        try {
            logger.info("Loading report template...");
            InputStream reportStream = getClass().getResourceAsStream("/reports/reportfile.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("Report file not found in /reports/reportfile.jrxml");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            logger.info("Report compiled successfully.");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("title", "Bảng Thông Tin Người Dùng");
            parameters.put("logoPath", getClass().getResource("/images/logo.png").toExternalForm());
            parameters.put("address", "123 Đường ABC, Quận 7, TP.HCM");
            parameters.put("phone", "0909090909");
            parameters.put("reportDate", new java.util.Date());

            logger.info("Filling report...");
            connection = reportDataSource.getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            logger.info("Report filled successfully.");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if (format.equalsIgnoreCase("pdf")) {
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                logger.info("PDF exported successfully.");
            } else if (format.equalsIgnoreCase("xlsx")) {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                exporter.exportReport();
                logger.info("Excel exported successfully.");
            } else {
                throw new RuntimeException("Unsupported format: " + format);
            }

            return outputStream.toByteArray();

        } catch (Exception e) {
            logger.error("Error exporting report: ", e);
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    logger.info("Database connection closed.");
                } catch (Exception e) {
                    logger.error("Error closing database connection: ", e);
                }
            }
        }
    }
}
