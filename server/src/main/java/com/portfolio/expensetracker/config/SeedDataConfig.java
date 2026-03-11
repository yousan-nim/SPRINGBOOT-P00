package com.portfolio.expensetracker.config;

import com.portfolio.expensetracker.entity.Category;
import com.portfolio.expensetracker.entity.CategoryType;
import com.portfolio.expensetracker.repository.CategoryRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDataConfig {

    @Bean
    CommandLineRunner seedCategories(CategoryRepository categoryRepository) {
        return args -> {
            if (categoryRepository.count() > 0) {
                return;
            }

            categoryRepository.saveAll(List.of(
                    new Category("Salary", CategoryType.INCOME, "#1D8348"),
                    new Category("Freelance", CategoryType.INCOME, "#117A65"),
                    new Category("Food", CategoryType.EXPENSE, "#D35400"),
                    new Category("Transport", CategoryType.EXPENSE, "#2E86C1"),
                    new Category("Utilities", CategoryType.EXPENSE, "#7D3C98"),
                    new Category("Entertainment", CategoryType.EXPENSE, "#C0392B")
            ));
        };
    }
}
