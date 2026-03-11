package com.portfolio.expensetracker.dto.category;

import com.portfolio.expensetracker.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank @Size(max = 60) String name,
        @NotNull CategoryType type,
        @NotBlank @Pattern(regexp = "^#([A-Fa-f0-9]{6})$") String color
) {
}
