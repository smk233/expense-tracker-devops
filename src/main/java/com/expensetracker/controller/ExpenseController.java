package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseDto;
import com.expensetracker.dto.ExpenseFilterDto;
import com.expensetracker.enums.Category;
import com.expensetracker.enums.PaymentMethod;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    @GetMapping
    public String listExpenses(
            @ModelAttribute ExpenseFilterDto filterDto,
            Model model) {

        log.debug("Fetching expenses with filter: {}", filterDto);
        Page<ExpenseDto> expensePage = expenseService.getExpenses(filterDto);

        model.addAttribute("expensePage", expensePage);
        model.addAttribute("filterDto", filterDto);
        model.addAttribute("categories", Category.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("activePage", "expenses");
        model.addAttribute("user", userService.getCurrentUser());

        // Pagination metadata
        model.addAttribute("currentPage", filterDto.getPage());
        model.addAttribute("totalPages", expensePage.getTotalPages());
        model.addAttribute("totalElements", expensePage.getTotalElements());
        model.addAttribute("pageSize", filterDto.getSize());

        return "expense/list";
    }

    @GetMapping("/add")
    public String addExpenseForm(Model model) {
        model.addAttribute("expenseDto", new ExpenseDto());
        model.addAttribute("categories", Category.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("activePage", "expenses");
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("formTitle", "Add New Expense");
        model.addAttribute("formAction", "/expenses/add");
        return "expense/form";
    }

    @PostMapping("/add")
    public String addExpense(
            @Valid @ModelAttribute("expenseDto") ExpenseDto expenseDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("activePage", "expenses");
            model.addAttribute("user", userService.getCurrentUser());
            model.addAttribute("formTitle", "Add New Expense");
            model.addAttribute("formAction", "/expenses/add");
            return "expense/form";
        }

        expenseService.createExpense(expenseDto);
        redirectAttributes.addFlashAttribute("successMessage", "Expense added successfully!");
        return "redirect:/expenses";
    }

    @GetMapping("/edit/{id}")
    public String editExpenseForm(@PathVariable Long id, Model model) {
        ExpenseDto expenseDto = expenseService.getExpenseById(id);
        model.addAttribute("expenseDto", expenseDto);
        model.addAttribute("categories", Category.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("activePage", "expenses");
        model.addAttribute("user", userService.getCurrentUser());
        model.addAttribute("formTitle", "Edit Expense");
        model.addAttribute("formAction", "/expenses/edit/" + id);
        return "expense/form";
    }

    @PostMapping("/edit/{id}")
    public String editExpense(
            @PathVariable Long id,
            @Valid @ModelAttribute("expenseDto") ExpenseDto expenseDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", Category.values());
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("activePage", "expenses");
            model.addAttribute("user", userService.getCurrentUser());
            model.addAttribute("formTitle", "Edit Expense");
            model.addAttribute("formAction", "/expenses/edit/" + id);
            return "expense/form";
        }

        expenseService.updateExpense(id, expenseDto);
        redirectAttributes.addFlashAttribute("successMessage", "Expense updated successfully!");
        return "redirect:/expenses";
    }

    @GetMapping("/view/{id}")
    public String viewExpense(@PathVariable Long id, Model model) {
        ExpenseDto expenseDto = expenseService.getExpenseById(id);
        model.addAttribute("expense", expenseDto);
        model.addAttribute("activePage", "expenses");
        model.addAttribute("user", userService.getCurrentUser());
        return "expense/view";
    }

    @PostMapping("/delete/{id}")
    public String deleteExpense(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        expenseService.deleteExpense(id);
        redirectAttributes.addFlashAttribute("successMessage", "Expense deleted successfully!");
        return "redirect:/expenses";
    }
}