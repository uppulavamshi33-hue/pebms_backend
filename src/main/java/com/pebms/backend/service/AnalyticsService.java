package com.pebms.backend.service;

import com.pebms.backend.dto.*;
import com.pebms.backend.model.Expense;
import com.pebms.backend.model.Income;
import com.pebms.backend.model.User;
import com.pebms.backend.repository.ExpenseRepository;
import com.pebms.backend.repository.IncomeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetService budgetService;
    private final AuthService authService;

    public AnalyticsService(IncomeRepository incomeRepository,
                            ExpenseRepository expenseRepository,
                            BudgetService budgetService,
                            AuthService authService) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.budgetService = budgetService;
        this.authService = authService;
    }

    public DashboardSummaryDto getDashboardSummary(Integer month, Integer year) {
        User user = authService.getCurrentAuthenticatedUser();
        LocalDate now = LocalDate.now();
        int targetMonth = (month != null && month >= 1 && month <= 12) ? month : now.getMonthValue();
        int targetYear = (year != null && year >= 2000) ? year : now.getYear();

        YearMonth ym = YearMonth.of(targetYear, targetMonth);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        BigDecimal lifetimeIncome = incomeRepository.getLifetimeTotalIncome(user.getId());
        BigDecimal lifetimeExpense = expenseRepository.getLifetimeTotalExpense(user.getId());
        if (lifetimeIncome == null) lifetimeIncome = BigDecimal.ZERO;
        if (lifetimeExpense == null) lifetimeExpense = BigDecimal.ZERO;
        BigDecimal lifetimeBalance = lifetimeIncome.subtract(lifetimeExpense);

        BigDecimal monthIncome = incomeRepository.getTotalIncomeBetween(user.getId(), monthStart, monthEnd);
        BigDecimal monthExpense = expenseRepository.getTotalExpenseBetween(user.getId(), monthStart, monthEnd);
        if (monthIncome == null) monthIncome = BigDecimal.ZERO;
        if (monthExpense == null) monthExpense = BigDecimal.ZERO;
        BigDecimal monthSavings = monthIncome.subtract(monthExpense);

        double savingsRate = 0.0;
        if (monthIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = monthSavings.divide(monthIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        DashboardSummaryDto dashboard = new DashboardSummaryDto();
        dashboard.setLifetimeIncome(lifetimeIncome);
        dashboard.setLifetimeExpense(lifetimeExpense);
        dashboard.setLifetimeBalance(lifetimeBalance);

        dashboard.setSelectedMonth(targetMonth);
        dashboard.setSelectedYear(targetYear);
        dashboard.setSelectedMonthIncome(monthIncome);
        dashboard.setSelectedMonthExpense(monthExpense);
        dashboard.setSelectedMonthSavings(monthSavings);
        dashboard.setSelectedMonthSavingsRate(Math.round(savingsRate * 100.0) / 100.0);

        dashboard.setMonthlyTrends(getMonthlyTrends(targetYear));
        dashboard.setExpenseCategoryBreakdown(getCategoryBreakdown("EXPENSE", targetMonth, targetYear));
        dashboard.setIncomeCategoryBreakdown(getCategoryBreakdown("INCOME", targetMonth, targetYear));
        dashboard.setBudgetStatuses(budgetService.getBudgetStatuses(targetMonth, targetYear));
        dashboard.setRecentTransactions(getRecentTransactions(10));

        return dashboard;
    }

    public List<MonthlySummaryDto> getMonthlyTrends(int year) {
        User user = authService.getCurrentAuthenticatedUser();
        List<MonthlySummaryDto> trends = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(year, m);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();

            BigDecimal inc = incomeRepository.getTotalIncomeBetween(user.getId(), start, end);
            BigDecimal exp = expenseRepository.getTotalExpenseBetween(user.getId(), start, end);
            if (inc == null) inc = BigDecimal.ZERO;
            if (exp == null) exp = BigDecimal.ZERO;
            BigDecimal net = inc.subtract(exp);

            double rate = 0.0;
            if (inc.compareTo(BigDecimal.ZERO) > 0) {
                rate = net.divide(inc, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }

            String monthName = Month.of(m).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            trends.add(new MonthlySummaryDto(
                    m,
                    monthName,
                    year,
                    inc,
                    exp,
                    net,
                    Math.round(rate * 100.0) / 100.0
            ));
        }

        return trends;
    }

    public List<CategoryBreakdownDto> getCategoryBreakdown(String type, int month, int year) {
        User user = authService.getCurrentAuthenticatedUser();
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<CategoryBreakdownDto> result = new ArrayList<>();

        if ("INCOME".equalsIgnoreCase(type)) {
            BigDecimal total = incomeRepository.getTotalIncomeBetween(user.getId(), start, end);
            if (total == null) total = BigDecimal.ZERO;
            List<Object[]> stats = incomeRepository.getCategoryStatsBetween(user.getId(), start, end);

            for (Object[] row : stats) {
                String cat = (String) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                Long count = (Long) row[2];

                double pct = 0.0;
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    pct = amount.divide(total, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }

                result.add(new CategoryBreakdownDto(cat, amount, Math.round(pct * 100.0) / 100.0, count, "INCOME"));
            }
        } else {
            BigDecimal total = expenseRepository.getTotalExpenseBetween(user.getId(), start, end);
            if (total == null) total = BigDecimal.ZERO;
            List<Object[]> stats = expenseRepository.getCategoryStatsBetween(user.getId(), start, end);

            for (Object[] row : stats) {
                String cat = (String) row[0];
                BigDecimal amount = (BigDecimal) row[1];
                Long count = (Long) row[2];

                double pct = 0.0;
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    pct = amount.divide(total, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();
                }

                result.add(new CategoryBreakdownDto(cat, amount, Math.round(pct * 100.0) / 100.0, count, "EXPENSE"));
            }
        }

        return result;
    }

    public List<TransactionDto> getRecentTransactions(int limit) {
        User user = authService.getCurrentAuthenticatedUser();
        List<Income> incomes = incomeRepository.findByUserIdOrderByDateDesc(user.getId());
        List<Expense> expenses = expenseRepository.findByUserIdOrderByDateDesc(user.getId());

        List<TransactionDto> unified = new ArrayList<>();
        for (Income i : incomes) {
            unified.add(new TransactionDto(
                    i.getId(), "INCOME", i.getTitle(), i.getAmount(), i.getCategory(), i.getDate(), i.getPaymentMethod(), i.getNotes()
            ));
        }
        for (Expense e : expenses) {
            unified.add(new TransactionDto(
                    e.getId(), "EXPENSE", e.getTitle(), e.getAmount(), e.getCategory(), e.getDate(), e.getPaymentMethod(), e.getNotes()
            ));
        }

        unified.sort((a, b) -> b.getDate().compareTo(a.getDate()));

        return unified.stream().limit(limit).collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactions(LocalDate startDate, LocalDate endDate, String type, String category) {
        User user = authService.getCurrentAuthenticatedUser();
        List<TransactionDto> result = new ArrayList<>();

        boolean includeIncome = type == null || "ALL".equalsIgnoreCase(type) || "INCOME".equalsIgnoreCase(type);
        boolean includeExpense = type == null || "ALL".equalsIgnoreCase(type) || "EXPENSE".equalsIgnoreCase(type);

        if (includeIncome) {
            List<Income> incomes = (startDate != null && endDate != null)
                    ? incomeRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), startDate, endDate)
                    : incomeRepository.findByUserIdOrderByDateDesc(user.getId());

            for (Income i : incomes) {
                if (category == null || category.equalsIgnoreCase("ALL") || i.getCategory().equalsIgnoreCase(category)) {
                    result.add(new TransactionDto(
                            i.getId(), "INCOME", i.getTitle(), i.getAmount(), i.getCategory(), i.getDate(), i.getPaymentMethod(), i.getNotes()
                    ));
                }
            }
        }

        if (includeExpense) {
            List<Expense> expenses = (startDate != null && endDate != null)
                    ? expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), startDate, endDate)
                    : expenseRepository.findByUserIdOrderByDateDesc(user.getId());

            for (Expense e : expenses) {
                if (category == null || category.equalsIgnoreCase("ALL") || e.getCategory().equalsIgnoreCase(category)) {
                    result.add(new TransactionDto(
                            e.getId(), "EXPENSE", e.getTitle(), e.getAmount(), e.getCategory(), e.getDate(), e.getPaymentMethod(), e.getNotes()
                    ));
                }
            }
        }

        result.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        return result;
    }
}
