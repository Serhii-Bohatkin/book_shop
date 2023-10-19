package com.example.bookshop.repository.book;

import com.example.bookshop.model.Book;
import com.example.bookshop.repository.SpecificationProvider;
import com.example.bookshop.repository.SpecificationProviderManager;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {
    private List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find a correct specification provider for key " + key));
    }
}
