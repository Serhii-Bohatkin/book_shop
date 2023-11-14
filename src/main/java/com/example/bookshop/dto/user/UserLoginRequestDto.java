package com.example.bookshop.dto.user;

import com.example.bookshop.validation.Email;
import com.example.bookshop.validation.Password;
import lombok.Data;

@Data
public class UserLoginRequestDto {
    @Email
    private String email;
    @Password
    private String password;
}
