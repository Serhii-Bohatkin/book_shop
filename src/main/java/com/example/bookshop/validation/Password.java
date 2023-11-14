package com.example.bookshop.validation;

import com.example.bookshop.validation.impl.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "The password must be between 8 and 20 characters in length and have:"
            + " 1) At least one capital English letter."
            + " 2) At least one lowercase English letter."
            + " 3) At least one number."
            + " 4) At least one special character.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
