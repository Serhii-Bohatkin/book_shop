package com.example.bookshop.dto.user;

import com.example.bookshop.validation.Email;
import com.example.bookshop.validation.FieldsMatch;
import com.example.bookshop.validation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@FieldsMatch(field = "password", fieldMatch = "repeatPassword",
        message = "Password and repeatPassword fields are not matching")
public class UserRegistrationRequestDto {
    @Email
    private String email;
    @Password
    private String password;
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
