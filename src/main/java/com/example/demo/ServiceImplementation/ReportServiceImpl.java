package com.example.demo.ServiceImplementation;

import com.example.demo.Service.ReportService;
import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final BookRepository bookRepository;

    private final DataSource dataSource;

    // Constructor injection of BookRepository
    public ReportServiceImpl(BookRepository bookRepository, DataSource dataSource) {
        super();
        this.bookRepository = bookRepository;
        this.dataSource = dataSource;
    }

    @Override
    public byte[] exportBookReport() throws Exception {
        // 1. Fetch all books from the database
        List<Book> books = bookRepository.findAll();

        // 2. Transform each Book object into a Map for report consumption
        List<Map<String, Object>> data = new ArrayList<>();

        for (Book book : books) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", book.getTitle());
            map.put("author", book.getAuthor());
            map.put("isbn", book.getIsbn());
            map.put("category", book.getCategory().getName());
            data.add(map);
        }

        // 3. Wrap the list of maps into a JRBeanCollectionDataSource for JasperReports
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);

        // 4. Load the JRXML report template from resources folder
        InputStream reportStream = getClass().getResourceAsStream("/reports/BookReport.jrxml");

        // 5. Compile the JRXML template to a JasperReport object
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // 6. Fill the report with data (no extra parameters, so use empty HashMap)
        JasperPrint print = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);

        // 7. Export the filled report to a PDF byte array and return
        return JasperExportManager.exportReportToPdf(print);
    }

    @Override
    public byte[] generateBookByIdReport(int bookId) throws Exception {

        // Step 1: Load the .jrxml file from resources/reports directory
        InputStream reportStream = getClass().getResourceAsStream("/reports/BookByIdReport.jrxml");

        // Step 2: Compile the .jrxml file to JasperReport object
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Step 3: Prepare parameter map to pass dynamic values into the report
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("bookId", bookId);  // This maps to $P{bookId} in your report's SQL

        // we can use built in data source to avoid writing code
        Connection connection = dataSource.getConnection();

        // Step 4: Fill the report using compiled report, parameter map, and MySQL connection
        JasperPrint print = JasperFillManager.fillReport(
                jasperReport,         // Compiled report
                parameters,           // Parameters (e.g. bookId)
                connection      // JDBC connection to fetch data using report SQL
//                getConnection()
        );

        // Step 5: Export the filled report to PDF format (returns byte[])
        return JasperExportManager.exportReportToPdf(print);
    }

    // Utility method to return a MySQL connection
    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/book_manager"; // DB URL
        String username = "root";                                // DB username
        String password = "Sajjad725523";                        // DB password

        // Load MySQL driver (use 'com.mysql.cj.jdbc.Driver' for Connector/J 8+)
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Return a JDBC connection
        return DriverManager.getConnection(url, username, password);
    }

}
