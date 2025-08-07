package com.example.demo.controller;

import com.example.demo.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

// Mark this class as a REST controller
@Controller
@RequestMapping("/report")  // Base path for all report-related endpoints
public class ReportController {

    private final ReportService reportService;  // Injecting ReportService

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping()
    public String reportsPage() {
        return "report"; // templates/report.html
    }

    // HTTP GET endpoint to generate and return the Book Report as PDF
    @GetMapping("/books")
    @ResponseBody
    public ResponseEntity<byte[]> getBookReport() throws Exception {
        // 1. Call service to generate the PDF report as byte array
        byte[] pdfBytes = reportService.exportBookReport();

        // 2. Create HTTP headers to tell the browser it's a PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF); // Content type
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("books.pdf") // Filename shown to browser
                .build());

        // 3. Return PDF as HTTP response with headers
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes); // The actual PDF content
    }

    // Handle HTTP GET request to /report/bookById?bookId=1
    @GetMapping("/bookById")
    public ResponseEntity<byte[]> getBookByIdReport(@RequestParam("bookId") int bookId) throws Exception {

        // Call the service layer to generate a Jasper report PDF as byte array for the given bookId
        byte[] pdfBytes = reportService.generateBookByIdReport(bookId);

        // Create HTTP headers to define the response type and filename
        HttpHeaders headers = new HttpHeaders();

        // Set the content type as PDF so browser knows how to render it
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Set the content disposition to inline so it opens in browser (not downloaded),
        // and also define the default filename
        headers.setContentDisposition(ContentDisposition.inline().filename("book_by_id_"+bookId+".pdf").build());

        // Return the PDF bytes in the HTTP response with headers set
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

}
