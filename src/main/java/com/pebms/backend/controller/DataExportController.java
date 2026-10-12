package com.pebms.backend.controller;

import com.pebms.backend.dto.TransactionDto;
import com.pebms.backend.service.AnalyticsService;
import com.pebms.backend.service.DataSeedService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataExportController {

    private final AnalyticsService analyticsService;
    private final DataSeedService dataSeedService;

    public DataExportController(AnalyticsService analyticsService, DataSeedService dataSeedService) {
        this.analyticsService = analyticsService;
        this.dataSeedService = dataSeedService;
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category
    ) {
        List<TransactionDto> transactions = analyticsService.getTransactions(null, null, type, category);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Type,Date,Title,Category,Amount,Payment Method,Notes\n");

        for (TransactionDto t : transactions) {
            csv.append(escapeSpecialChars(String.valueOf(t.getId()))).append(",");
            csv.append(escapeSpecialChars(t.getType())).append(",");
            csv.append(escapeSpecialChars(String.valueOf(t.getDate()))).append(",");
            csv.append(escapeSpecialChars(t.getTitle())).append(",");
            csv.append(escapeSpecialChars(t.getCategory())).append(",");
            csv.append(t.getAmount() != null ? t.getAmount().toPlainString() : "0").append(",");
            csv.append(escapeSpecialChars(t.getPaymentMethod() != null ? t.getPaymentMethod() : "")).append(",");
            csv.append(escapeSpecialChars(t.getNotes() != null ? t.getNotes() : "")).append("\n");
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        String filename = "pebms_transactions_" + LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    @PostMapping("/seed")
    public ResponseEntity<Map<String, String>> seedData(@RequestParam(defaultValue = "true") boolean overwrite) {
        dataSeedService.seedSampleData(overwrite);
        return ResponseEntity.ok(Map.of("message", "Sample data successfully seeded!"));
    }

    private String escapeSpecialChars(String data) {
        if (data == null) return "\"\"";
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
