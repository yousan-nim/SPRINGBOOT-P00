package com.portfolio.expensetracker.repository;

import com.portfolio.expensetracker.entity.TransactionRecord;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Long> {

    List<TransactionRecord> findAllByOrderByTransactionDateDescCreatedAtDesc();

    List<TransactionRecord> findAllByTransactionDateBetweenOrderByTransactionDateDescCreatedAtDesc(
            LocalDate startDate,
            LocalDate endDate
    );
}
