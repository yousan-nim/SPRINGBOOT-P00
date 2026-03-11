package com.portfolio.expensetracker.dto.transaction;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public record MonthlySummaryResponse(
        YearMonth month,
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        int transactionCount,
        Map<String, BigDecimal> expenseByCategory
) {
}
