package com.expensetracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardDto {

    private BigDecimal totalExpenses;
    private BigDecimal monthlyExpenses;
    private long totalCount;
    private Map<String, BigDecimal> categoryWiseExpenses;
    private List<ExpenseDto> recentExpenses;
    private Map<String, BigDecimal> monthlyBreakdown;
    private String currentMonth;
    private int currentYear;
}