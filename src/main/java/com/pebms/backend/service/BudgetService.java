package com.pebms.backend.service;

import com.pebms.backend.dto.BudgetDto;
import com.pebms.backend.dto.BudgetStatusDto;
import com.pebms.backend.exception.ResourceNotFoundException;
import com.pebms.backend.model.Budget;
import com.pebms.backend.model.User;
import com.pebms.backend.repository.BudgetRepository;
import com.pebms.backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final AuthService authService;

    public BudgetService(BudgetRepository budgetRepository,
                         ExpenseRepository expenseRepository,
                         AuthService authService) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
        this.authService = authService;
    }

    public List<BudgetDto> getAllBudgets() {
        User user = authService.getCurrentAuthenticatedUser();
        return budgetRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BudgetDto> getBudgetsByMonthAndYear(Integer month, Integer year) {
        User user = authService.getCurrentAuthenticatedUser();
        return budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BudgetDto getBudgetById(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        if (budget.getUser() != null && !budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to requested budget record");
        }
        return mapToDto(budget);
    }

    public BudgetDto saveOrUpdateBudget(BudgetDto dto) {
        User user = authService.getCurrentAuthenticatedUser();

        Optional<Budget> existingOpt = budgetRepository.findByUserIdAndCategoryIgnoreCaseAndMonthAndYear(
                user.getId(), dto.getCategory(), dto.getMonth(), dto.getYear());

        Budget budget;
        if (existingOpt.isPresent()) {
            budget = existingOpt.get();
            budget.setMonthlyLimit(dto.getMonthlyLimit());
            budget.setNotes(dto.getNotes());
        } else {
            budget = new Budget();
            budget.setUser(user);
            budget.setCategory(dto.getCategory());
            budget.setMonth(dto.getMonth());
            budget.setYear(dto.getYear());
            budget.setMonthlyLimit(dto.getMonthlyLimit());
            budget.setNotes(dto.getNotes());
        }

        Budget saved = budgetRepository.save(budget);
        return mapToDto(saved);
    }

    public void deleteBudget(Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + id));

        if (budget.getUser() != null && !budget.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied to delete this budget record");
        }

        budgetRepository.deleteById(id);
    }

    public List<BudgetStatusDto> getBudgetStatuses(Integer month, Integer year) {
        User user = authService.getCurrentAuthenticatedUser();
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(user.getId(), month, year);

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<BudgetStatusDto> statuses = new ArrayList<>();

        for (Budget b : budgets) {
            BigDecimal actualSpent;
            if ("OVERALL".equalsIgnoreCase(b.getCategory())) {
                actualSpent = expenseRepository.getTotalExpenseBetween(user.getId(), startDate, endDate);
            } else {
                actualSpent = expenseRepository.getTotalExpenseByCategoryBetween(user.getId(), b.getCategory(), startDate, endDate);
            }

            if (actualSpent == null) {
                actualSpent = BigDecimal.ZERO;
            }

            BigDecimal remaining = b.getMonthlyLimit().subtract(actualSpent);
            double percentage = 0.0;
            if (b.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {
                percentage = actualSpent.divide(b.getMonthlyLimit(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }

            boolean isOver = actualSpent.compareTo(b.getMonthlyLimit()) > 0;
            String status;
            if (isOver) {
                status = "EXCEEDED";
            } else if (percentage >= 80.0) {
                status = "WARNING";
            } else {
                status = "NORMAL";
            }

            statuses.add(new BudgetStatusDto(
                    b.getId(),
                    b.getCategory(),
                    b.getMonth(),
                    b.getYear(),
                    b.getMonthlyLimit(),
                    actualSpent,
                    remaining,
                    Math.round(percentage * 100.0) / 100.0,
                    isOver,
                    status
            ));
        }

        return statuses;
    }

    public BudgetDto mapToDto(Budget entity) {
        return new BudgetDto(
                entity.getId(),
                entity.getCategory(),
                entity.getMonth(),
                entity.getYear(),
                entity.getMonthlyLimit(),
                entity.getNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
