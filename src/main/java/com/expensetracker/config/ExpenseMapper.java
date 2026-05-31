package com.expensetracker.util;

import com.expensetracker.dto.ExpenseDto;
import com.expensetracker.entity.Expense;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public Expense toEntity(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setTitle(dto.getTitle());
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setPaymentMethod(dto.getPaymentMethod());
        expense.setExpenseDate(dto.getExpenseDate());
        return expense;
    }

    public ExpenseDto toDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setTitle(expense.getTitle());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setCategory(expense.getCategory());
        dto.setPaymentMethod(expense.getPaymentMethod());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setCreatedAt(expense.getCreatedAt());
        dto.setUpdatedAt(expense.getUpdatedAt());
        if (expense.getUser() != null) {
            dto.setUserName(expense.getUser().getFullName());
            dto.setUserId(expense.getUser().getId());
        }
        return dto;
    }

    public void updateEntityFromDto(ExpenseDto dto, Expense expense) {
        expense.setTitle(dto.getTitle());
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setCategory(dto.getCategory());
        expense.setPaymentMethod(dto.getPaymentMethod());
        expense.setExpenseDate(dto.getExpenseDate());
    }
}