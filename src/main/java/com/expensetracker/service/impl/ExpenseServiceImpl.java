package com.expensetracker.service.impl;

import com.expensetracker.dto.DashboardDto;
import com.expensetracker.dto.ExpenseDto;
import com.expensetracker.dto.ExpenseFilterDto;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.exception.UnauthorizedException;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;
import com.expensetracker.util.ExpenseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final ExpenseMapper expenseMapper;

    @Override
    public ExpenseDto createExpense(ExpenseDto expenseDto) {
        User currentUser = userService.getCurrentUser();
        log.info("Creating expense '{}' for user: {}", expenseDto.getTitle(), currentUser.getEmail());

        Expense expense = expenseMapper.toEntity(expenseDto);
        expense.setUser(currentUser);
        Expense saved = expenseRepository.save(expense);
        log.info("Expense created with ID: {}", saved.getId());
        return expenseMapper.toDto(saved);
    }

    @Override
    public ExpenseDto updateExpense(Long id, ExpenseDto expenseDto) {
        Long userId = userService.getCurrentUserId();
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        log.info("Updating expense ID: {} for user ID: {}", id, userId);
        expenseMapper.updateEntityFromDto(expenseDto, expense);
        Expense updated = expenseRepository.save(expense);
        return expenseMapper.toDto(updated);
    }

    @Override
    public void deleteExpense(Long id) {
        Long userId = userService.getCurrentUserId();
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        log.info("Deleting expense ID: {} for user ID: {}", id, userId);
        expenseRepository.delete(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseDto getExpenseById(Long id) {
        Long userId = userService.getCurrentUserId();
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        return expenseMapper.toDto(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenseDto> getExpenses(ExpenseFilterDto filterDto) {
        Long userId = userService.getCurrentUserId();

        Sort sort = Sort.by(
                filterDto.getSortDir().equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC : Sort.Direction.DESC,
                filterDto.getSortBy()
        );
        Pageable pageable = PageRequest.of(filterDto.getPage(), filterDto.getSize(), sort);

        Page<Expense> expensePage = expenseRepository.findWithFilters(
                userId,
                filterDto.getKeyword(),
                filterDto.getCategory(),
                filterDto.getPaymentMethod(),
                filterDto.getStartDate(),
                filterDto.getEndDate(),
                filterDto.getMinAmount(),
                filterDto.getMaxAmount(),
                pageable
        );

        return expensePage.map(expenseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardDto getDashboardData() {
        Long userId = userService.getCurrentUserId();
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        DashboardDto dashboard = new DashboardDto();

        // Totals
        dashboard.setTotalExpenses(expenseRepository.getTotalExpensesByUserId(userId));
        dashboard.setMonthlyExpenses(expenseRepository.getMonthlyExpenses(userId, currentMonth, currentYear));
        dashboard.setTotalCount(expenseRepository.countByUserId(userId));

        // Category wise
        List<Object[]> categoryData = expenseRepository.getCategoryWiseExpenses(userId);
        Map<String, BigDecimal> categoryMap = new LinkedHashMap<>();
        for (Object[] row : categoryData) {
            String catName = row[0].toString();
            BigDecimal amount = (BigDecimal) row[1];
            categoryMap.put(catName, amount);
        }
        dashboard.setCategoryWiseExpenses(categoryMap);

        // Recent expenses (last 5)
        Pageable recentPageable = PageRequest.of(0, 5);
        List<Expense> recent = expenseRepository.findRecentExpenses(userId, recentPageable);
        dashboard.setRecentExpenses(recent.stream().map(expenseMapper::toDto).toList());

        // Monthly breakdown for current year
        List<Object[]> monthlyData = expenseRepository.getMonthlyBreakdown(userId, currentYear);
        Map<String, BigDecimal> monthlyMap = new LinkedHashMap<>();
        for (Object[] row : monthlyData) {
            int month = ((Number) row[0]).intValue();
            BigDecimal amount = (BigDecimal) row[1];
            String monthName = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthlyMap.put(monthName, amount);
        }
        dashboard.setMonthlyBreakdown(monthlyMap);

        dashboard.setCurrentMonth(Month.of(currentMonth).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        dashboard.setCurrentYear(currentYear);

        return dashboard;
    }
}