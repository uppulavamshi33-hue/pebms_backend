package com.pebms.backend.repository;

import com.pebms.backend.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    List<Budget> findByUserIdAndYear(Long userId, Integer year);

    List<Budget> findByUserId(Long userId);

    Optional<Budget> findByUserIdAndCategoryIgnoreCaseAndMonthAndYear(Long userId, String category, Integer month, Integer year);

    // Fallbacks
    List<Budget> findByMonthAndYear(Integer month, Integer year);

    List<Budget> findByYear(Integer year);

    Optional<Budget> findByCategoryIgnoreCaseAndMonthAndYear(String category, Integer month, Integer year);
}
