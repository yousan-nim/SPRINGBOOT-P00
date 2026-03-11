package com.portfolio.expensetracker.service;

import com.portfolio.expensetracker.dto.category.CategoryResponse;
import com.portfolio.expensetracker.dto.transaction.MonthlySummaryResponse;
import com.portfolio.expensetracker.dto.transaction.TransactionRequest;
import com.portfolio.expensetracker.dto.transaction.TransactionResponse;
import com.portfolio.expensetracker.entity.Category;
import com.portfolio.expensetracker.entity.TransactionRecord;
import com.portfolio.expensetracker.entity.TransactionType;
import com.portfolio.expensetracker.exception.ApiException;
import com.portfolio.expensetracker.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll(YearMonth month) {
        List<TransactionRecord> records = month == null
                ? transactionRepository.findAllByOrderByTransactionDateDescCreatedAtDesc()
                : transactionRepository.findAllByTransactionDateBetweenOrderByTransactionDateDescCreatedAtDesc(
                        month.atDay(1),
                        month.atEndOfMonth()
                );

        return records.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        Category category = validateCategory(request.categoryId(), request.type());

        TransactionRecord record = new TransactionRecord();
        apply(record, request, category);
        return toResponse(transactionRepository.save(record));
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request) {
        TransactionRecord record = transactionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transaction not found"));
        Category category = validateCategory(request.categoryId(), request.type());

        apply(record, request, category);
        return toResponse(transactionRepository.save(record));
    }

    @Transactional
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(YearMonth month) {
        YearMonth targetMonth = month == null ? YearMonth.now() : month;
        List<TransactionRecord> records = transactionRepository.findAllByTransactionDateBetweenOrderByTransactionDateDescCreatedAtDesc(
                targetMonth.atDay(1),
                targetMonth.atEndOfMonth()
        );

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        LinkedHashMap<String, BigDecimal> expenseByCategory = new LinkedHashMap<>();

        for (TransactionRecord record : records) {
            if (record.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(record.getAmount());
            } else {
                totalExpense = totalExpense.add(record.getAmount());
                expenseByCategory.merge(record.getCategory().getName(), record.getAmount(), BigDecimal::add);
            }
        }

        return new MonthlySummaryResponse(
                targetMonth,
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense),
                records.size(),
                expenseByCategory
        );
    }

    private Category validateCategory(Long categoryId, TransactionType type) {
        Category category = categoryService.getById(categoryId);
        if (!category.getType().name().equals(type.name())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Category type does not match transaction type");
        }
        return category;
    }

    private void apply(TransactionRecord record, TransactionRequest request, Category category) {
        record.setTitle(request.title().trim());
        record.setAmount(request.amount());
        record.setType(request.type());
        record.setTransactionDate(request.transactionDate());
        record.setNote(request.note() == null || request.note().isBlank() ? null : request.note().trim());
        record.setCategory(category);
    }

    private TransactionResponse toResponse(TransactionRecord record) {
        return new TransactionResponse(
                record.getId(),
                record.getTitle(),
                record.getAmount(),
                record.getType(),
                record.getTransactionDate(),
                record.getNote(),
                CategoryResponse.from(record.getCategory()),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}
