package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.dto.category.CategoryRequest;
import com.portfolio.expensetracker.dto.category.CategoryResponse;
import com.portfolio.expensetracker.entity.Category;
import com.portfolio.expensetracker.exception.ApiException;
import com.portfolio.expensetracker.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllByOrderByTypeAscNameAsc()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Category not found"));
    }

    public CategoryResponse create(CategoryRequest request) {
        categoryRepository.findByNameIgnoreCase(request.name())
                .ifPresent(existing -> {
                    throw new ApiException(HttpStatus.CONFLICT, "Category name already exists");
                });

        Category category = new Category(request.name().trim(), request.type(), request.color().toUpperCase());
        return CategoryResponse.from(categoryRepository.save(category));
    }
}
