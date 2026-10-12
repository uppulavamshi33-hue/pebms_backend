package com.pebms.backend.dto;

import java.math.BigDecimal;

public class BudgetStatusDto {
    private Long id;
    private String category;
    private Integer month;
    private Integer year;
    private BigDecimal monthlyLimit;
    private BigDecimal actualSpent;
    private BigDecimal remainingAmount;
    private Double percentageUsed;
    private Boolean isOverBudget;
    private String status; // "NORMAL", "WARNING" (>=80%), "EXCEEDED" (>100%)

    public BudgetStatusDto() {}

    public BudgetStatusDto(Long id, String category, Integer month, Integer year, BigDecimal monthlyLimit,
                           BigDecimal actualSpent, BigDecimal remainingAmount, Double percentageUsed,
                           Boolean isOverBudget, String status) {
        this.id = id;
        this.category = category;
        this.month = month;
        this.year = year;
        this.monthlyLimit = monthlyLimit;
        this.actualSpent = actualSpent;
        this.remainingAmount = remainingAmount;
        this.percentageUsed = percentageUsed;
        this.isOverBudget = isOverBudget;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }
    public BigDecimal getActualSpent() { return actualSpent; }
    public void setActualSpent(BigDecimal actualSpent) { this.actualSpent = actualSpent; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    public Double getPercentageUsed() { return percentageUsed; }
    public void setPercentageUsed(Double percentageUsed) { this.percentageUsed = percentageUsed; }
    public Boolean getIsOverBudget() { return isOverBudget; }
    public void setIsOverBudget(Boolean isOverBudget) { this.isOverBudget = isOverBudget; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
