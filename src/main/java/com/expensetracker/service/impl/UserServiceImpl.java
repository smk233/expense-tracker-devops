package com.expensetracker.service.impl;

import com.expensetracker.dto.RegistrationDto;
import com.expensetracker.entity.Role;
import com.expensetracker.entity.User;
import com.expensetracker.exception.EmailAlreadyExistsException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.RoleRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegistrationDto registrationDto) {
        log.info("Registering new user with email: {}", registrationDto.getEmail());

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered: " + registrationDto.getEmail());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .email(registrationDto.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .enabled(true)
                .roles(roles)
                .build();

        User saved = userRepository.save(user);
        log.info("User registered successfully with ID: {}", saved.getId());
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        return auth.getName();
    }
}