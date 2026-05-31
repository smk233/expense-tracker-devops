package com.expensetracker.controller;

import com.expensetracker.dto.DashboardDto;
import com.expensetracker.entity.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final ExpenseService expenseService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.debug("Loading dashboard");
        User currentUser = userService.getCurrentUser();
        DashboardDto dashboardData = expenseService.getDashboardData();

        model.addAttribute("dashboard", dashboardData);
        model.addAttribute("user", currentUser);
        model.addAttribute("activePage", "dashboard");

        // Prepare chart data as JSON-safe structures
        Map<String, Object> categoryChartData = new HashMap<>();
        List<String> catLabels = new ArrayList<>(dashboardData.getCategoryWiseExpenses().keySet());
        List<Number> catValues = new ArrayList<>(dashboardData.getCategoryWiseExpenses().values());
        categoryChartData.put("labels", catLabels);
        categoryChartData.put("values", catValues);
        model.addAttribute("categoryChartData", categoryChartData);

        Map<String, Object> monthlyChartData = new HashMap<>();
        List<String> monthLabels = new ArrayList<>(dashboardData.getMonthlyBreakdown().keySet());
        List<Number> monthValues = new ArrayList<>(dashboardData.getMonthlyBreakdown().values());
        monthlyChartData.put("labels", monthLabels);
        monthlyChartData.put("values", monthValues);
        model.addAttribute("monthlyChartData", monthlyChartData);

        return "dashboard/dashboard";
    }
}