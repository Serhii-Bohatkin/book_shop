package com.example.bookshop.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
}
