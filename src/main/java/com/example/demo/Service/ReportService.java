package com.example.demo.Service;

import net.sf.jasperreports.engine.JRException;

public interface ReportService {
    byte[] exportBookReport() throws Exception;
    byte[] generateBookByIdReport(int bookId) throws Exception;
}
