package com.expensetracker.service;

import com.expensetracker.dto.RegistrationDto;
import com.expensetracker.entity.User;

public interface UserService {

    User registerUser(RegistrationDto registrationDto);

    User findByEmail(String email);

    boolean existsByEmail(String email);

    User getCurrentUser();

    Long getCurrentUserId();
}