package com.pebms.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryDto {
    private BigDecimal lifetimeIncome;
    private BigDecimal lifetimeExpense;
    private BigDecimal lifetimeBalance;

    private Integer selectedMonth;
    private Integer selectedYear;
    private BigDecimal selectedMonthIncome;
    private BigDecimal selectedMonthExpense;
    private BigDecimal selectedMonthSavings;
    private Double selectedMonthSavingsRate;

    private List<MonthlySummaryDto> monthlyTrends;
    private List<CategoryBreakdownDto> expenseCategoryBreakdown;
    private List<CategoryBreakdownDto> incomeCategoryBreakdown;
    private List<BudgetStatusDto> budgetStatuses;
    private List<TransactionDto> recentTransactions;

    public DashboardSummaryDto() {}

    public BigDecimal getLifetimeIncome() { return lifetimeIncome; }
    public void setLifetimeIncome(BigDecimal lifetimeIncome) { this.lifetimeIncome = lifetimeIncome; }

    public BigDecimal getLifetimeExpense() { return lifetimeExpense; }
    public void setLifetimeExpense(BigDecimal lifetimeExpense) { this.lifetimeExpense = lifetimeExpense; }

    public BigDecimal getLifetimeBalance() { return lifetimeBalance; }
    public void setLifetimeBalance(BigDecimal lifetimeBalance) { this.lifetimeBalance = lifetimeBalance; }

    public Integer getSelectedMonth() { return selectedMonth; }
    public void setSelectedMonth(Integer selectedMonth) { this.selectedMonth = selectedMonth; }

    public Integer getSelectedYear() { return selectedYear; }
    public void setSelectedYear(Integer selectedYear) { this.selectedYear = selectedYear; }

    public BigDecimal getSelectedMonthIncome() { return selectedMonthIncome; }
    public void setSelectedMonthIncome(BigDecimal selectedMonthIncome) { this.selectedMonthIncome = selectedMonthIncome; }

    public BigDecimal getSelectedMonthExpense() { return selectedMonthExpense; }
    public void setSelectedMonthExpense(BigDecimal selectedMonthExpense) { this.selectedMonthExpense = selectedMonthExpense; }

    public BigDecimal getSelectedMonthSavings() { return selectedMonthSavings; }
    public void setSelectedMonthSavings(BigDecimal selectedMonthSavings) { this.selectedMonthSavings = selectedMonthSavings; }

    public Double getSelectedMonthSavingsRate() { return selectedMonthSavingsRate; }
    public void setSelectedMonthSavingsRate(Double selectedMonthSavingsRate) { this.selectedMonthSavingsRate = selectedMonthSavingsRate; }

    public List<MonthlySummaryDto> getMonthlyTrends() { return monthlyTrends; }
    public void setMonthlyTrends(List<MonthlySummaryDto> monthlyTrends) { this.monthlyTrends = monthlyTrends; }

    public List<CategoryBreakdownDto> getExpenseCategoryBreakdown() { return expenseCategoryBreakdown; }
    public void setExpenseCategoryBreakdown(List<CategoryBreakdownDto> expenseCategoryBreakdown) { this.expenseCategoryBreakdown = expenseCategoryBreakdown; }

    public List<CategoryBreakdownDto> getIncomeCategoryBreakdown() { return incomeCategoryBreakdown; }
    public void setIncomeCategoryBreakdown(List<CategoryBreakdownDto> incomeCategoryBreakdown) { this.incomeCategoryBreakdown = incomeCategoryBreakdown; }

    public List<BudgetStatusDto> getBudgetStatuses() { return budgetStatuses; }
    public void setBudgetStatuses(List<BudgetStatusDto> budgetStatuses) { this.budgetStatuses = budgetStatuses; }

    public List<TransactionDto> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<TransactionDto> recentTransactions) { this.recentTransactions = recentTransactions; }
}
