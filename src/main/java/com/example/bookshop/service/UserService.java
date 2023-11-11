package com.example.bookshop.service;

import com.example.bookshop.dto.user.UserRegistrationRequestDto;
import com.example.bookshop.dto.user.UserResponseDto;
import com.example.bookshop.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;
}
