package com.portfolio.expensetracker.dto.category;

import com.portfolio.expensetracker.entity.Category;
import com.portfolio.expensetracker.entity.CategoryType;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        String color
) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getColor()
        );
    }
}
