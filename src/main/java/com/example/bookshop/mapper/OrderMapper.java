package com.example.bookshop.mapper;

import com.example.bookshop.config.MapperConfig;
import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderDto toDto(Order order);
}
