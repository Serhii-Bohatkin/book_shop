package com.example.bookshop.dto.category;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
}
