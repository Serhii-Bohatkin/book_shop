package com.example.bookshop.dto.cartitem;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class UpdateCartItemDto {
    @Min(1)
    private int quantity;
}
