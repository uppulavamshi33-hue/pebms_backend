package com.pebms.backend.dto;

import java.math.BigDecimal;

public class CategoryBreakdownDto {
    private String category;
    private BigDecimal totalAmount;
    private Double percentage;
    private Long transactionCount;
    private String type; // "EXPENSE" or "INCOME"

    public CategoryBreakdownDto() {}

    public CategoryBreakdownDto(String category, BigDecimal totalAmount, Double percentage, Long transactionCount, String type) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
        this.transactionCount = transactionCount;
        this.type = type;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    public Long getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Long transactionCount) { this.transactionCount = transactionCount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
