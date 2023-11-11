package com.example.bookshop.repository.book;

import com.example.bookshop.dto.book.BookSearchParametersDto;
import com.example.bookshop.model.Book;
import com.example.bookshop.repository.SpecificationBuilder;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private BookSpecificationProviderManager bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("title")
                    .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("author")
                    .getSpecification(searchParameters.authors()));
        }
        if (searchParameters.isbns() != null && searchParameters.isbns().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("isbn")
                    .getSpecification(searchParameters.isbns()));
        }
        if (searchParameters.minPrice() != null && searchParameters.minPrice().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("min_price")
                    .getSpecification(searchParameters.minPrice()));
        }
        if (searchParameters.maxPrice() != null && searchParameters.maxPrice().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("max_price")
                    .getSpecification(searchParameters.maxPrice()));
        }
        if (searchParameters.descriptions() != null && searchParameters.descriptions().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider("description")
                    .getSpecification(searchParameters.descriptions()));
        }
        return spec;
    }
}
