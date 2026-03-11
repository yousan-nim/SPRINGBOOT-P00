package com.portfolio.expensetracker.dto.transaction;

import com.portfolio.expensetracker.dto.category.CategoryResponse;
import com.portfolio.expensetracker.entity.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        String title,
        BigDecimal amount,
        TransactionType type,
        LocalDate transactionDate,
        String note,
        CategoryResponse category,
        Instant createdAt,
        Instant updatedAt
) {
}
