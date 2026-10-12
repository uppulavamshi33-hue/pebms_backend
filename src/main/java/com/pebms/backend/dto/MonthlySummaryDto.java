package com.pebms.backend.dto;

import java.math.BigDecimal;

public class MonthlySummaryDto {
    private Integer month;
    private String monthName;
    private Integer year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private Double savingsRate; // (income - expense) / income * 100

    public MonthlySummaryDto() {}

    public MonthlySummaryDto(Integer month, String monthName, Integer year, BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netSavings, Double savingsRate) {
        this.month = month;
        this.monthName = monthName;
        this.year = year;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netSavings = netSavings;
        this.savingsRate = savingsRate;
    }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public String getMonthName() { return monthName; }
    public void setMonthName(String monthName) { this.monthName = monthName; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
    public BigDecimal getNetSavings() { return netSavings; }
    public void setNetSavings(BigDecimal netSavings) { this.netSavings = netSavings; }
    public Double getSavingsRate() { return savingsRate; }
    public void setSavingsRate(Double savingsRate) { this.savingsRate = savingsRate; }
}
