package com.example.bookshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String author;
    @NotEmpty
    private String isbn;
    @NotNull
    @Min(0)
    private BigDecimal price;
    private String description;
    private String coverImage;
}
