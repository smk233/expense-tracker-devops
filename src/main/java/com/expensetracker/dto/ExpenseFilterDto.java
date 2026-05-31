package com.expensetracker.dto;

import com.expensetracker.enums.Category;
import com.expensetracker.enums.PaymentMethod;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseFilterDto {

    private String keyword;
    private Category category;
    private PaymentMethod paymentMethod;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private BigDecimal minAmount;
    private BigDecimal maxAmount;

    private int page = 0;
    private int size = 10;
    private String sortBy = "expenseDate";
    private String sortDir = "desc";
}