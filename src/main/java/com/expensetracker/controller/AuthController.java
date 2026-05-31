package com.expensetracker.controller;

import com.expensetracker.dto.RegistrationDto;
import com.expensetracker.exception.EmailAlreadyExistsException;
import com.expensetracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (expired != null) {
            model.addAttribute("errorMessage", "Your session has expired. Please log in again.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registrationDto") RegistrationDto registrationDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Check validation errors
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        // Check password matching
        if (!registrationDto.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword",
                    "Passwords do not match");
            return "auth/register";
        }

        try {
            userService.registerUser(registrationDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "auth/register";
        }
    }
}
