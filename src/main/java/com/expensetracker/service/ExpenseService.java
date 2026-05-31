package com.expensetracker.service;

import com.expensetracker.dto.DashboardDto;
import com.expensetracker.dto.ExpenseDto;
import com.expensetracker.dto.ExpenseFilterDto;
import org.springframework.data.domain.Page;

public interface ExpenseService {

    ExpenseDto createExpense(ExpenseDto expenseDto);

    ExpenseDto updateExpense(Long id, ExpenseDto expenseDto);

    void deleteExpense(Long id);

    ExpenseDto getExpenseById(Long id);

    Page<ExpenseDto> getExpenses(ExpenseFilterDto filterDto);

    DashboardDto getDashboardData();
}