package com.example.bookshop.dto.user;

import com.example.bookshop.validation.FieldsMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@FieldsMatch(field = "password", fieldMatch = "repeatPassword",
        message = "Password and repeatPassword fields are not matching")
public class UserRegistrationRequestDto {
    @Email(regexp = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$",
            message = "Invalid email")
    private String email;
    @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,20}$",
            message = """
                    The password must be between 8 and 20 characters in length and have:
                    * At least one capital English letter
                    * At least one lowercase English letter
                    * At least one number
                    * At least one special character""")
    private String password;
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
