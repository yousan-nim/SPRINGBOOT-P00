package com.portfolio.expensetracker.controller;

import com.portfolio.expensetracker.dto.transaction.MonthlySummaryResponse;
import com.portfolio.expensetracker.service.TransactionService;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TransactionService transactionService;

    @GetMapping("/summary")
    public MonthlySummaryResponse getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM")
            YearMonth month
    ) {
        return transactionService.getMonthlySummary(month);
    }
}
