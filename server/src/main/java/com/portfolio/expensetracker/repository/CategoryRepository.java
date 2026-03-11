package com.portfolio.expensetracker.repository;

import com.portfolio.expensetracker.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByOrderByTypeAscNameAsc();

    Optional<Category> findByNameIgnoreCase(String name);
}
