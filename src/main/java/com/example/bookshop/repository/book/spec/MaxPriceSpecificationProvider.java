package com.example.bookshop.repository.book.spec;

import com.example.bookshop.model.Book;
import com.example.bookshop.repository.SpecificationProvider;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class MaxPriceSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "max_price";
    }

    public Specification<Book> getSpecification(String[] params) {
        return (root, query, criteriaBuilder)
                -> criteriaBuilder.lt(root.get("price"), new BigDecimal(params[0]));
    }
}
