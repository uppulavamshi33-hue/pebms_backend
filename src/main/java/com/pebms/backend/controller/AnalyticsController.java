package com.pebms.backend.controller;

import com.pebms.backend.dto.CategoryBreakdownDto;
import com.pebms.backend.dto.DashboardSummaryDto;
import com.pebms.backend.dto.MonthlySummaryDto;
import com.pebms.backend.dto.TransactionDto;
import com.pebms.backend.service.AnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(analyticsService.getDashboardSummary(month, year));
    }

    @GetMapping("/monthly-trends")
    public ResponseEntity<List<MonthlySummaryDto>> getMonthlyTrends(
            @RequestParam(required = false) Integer year
    ) {
        int targetYear = (year != null && year >= 2000) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(analyticsService.getMonthlyTrends(targetYear));
    }

    @GetMapping("/category-breakdown")
    public ResponseEntity<List<CategoryBreakdownDto>> getCategoryBreakdown(
            @RequestParam(defaultValue = "EXPENSE") String type,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        int targetMonth = (month != null && month >= 1 && month <= 12) ? month : LocalDate.now().getMonthValue();
        int targetYear = (year != null && year >= 2000) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(analyticsService.getCategoryBreakdown(type, targetMonth, targetYear));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(analyticsService.getTransactions(startDate, endDate, type, category));
    }
}
