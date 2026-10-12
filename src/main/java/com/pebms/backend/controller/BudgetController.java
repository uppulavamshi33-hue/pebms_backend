package com.pebms.backend.controller;

import com.pebms.backend.dto.BudgetDto;
import com.pebms.backend.dto.BudgetStatusDto;
import com.pebms.backend.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public ResponseEntity<List<BudgetDto>> getBudgets(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        if (month != null && year != null) {
            return ResponseEntity.ok(budgetService.getBudgetsByMonthAndYear(month, year));
        }
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @GetMapping("/status")
    public ResponseEntity<List<BudgetStatusDto>> getBudgetStatuses(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        LocalDate now = LocalDate.now();
        int targetMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();
        int targetYear = (year != null && year >= 2000) ? year : now.getYear();

        return ResponseEntity.ok(budgetService.getBudgetStatuses(targetMonth, targetYear));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetDto> getBudgetById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @PostMapping
    public ResponseEntity<BudgetDto> saveBudget(@Valid @RequestBody BudgetDto dto) {
        BudgetDto saved = budgetService.saveOrUpdateBudget(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
