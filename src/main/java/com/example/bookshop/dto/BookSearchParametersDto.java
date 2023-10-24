package com.example.bookshop.dto;

public record BookSearchParametersDto(String[] titles, String[] authors, String[] isbns,
                                      String[] minPrice, String[] maxPrice, String[] descriptions) {
}
