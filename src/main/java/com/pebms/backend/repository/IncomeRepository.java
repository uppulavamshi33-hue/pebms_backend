package com.pebms.backend.repository;

import com.pebms.backend.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    List<Income> findAllByOrderByDateDesc();

    List<Income> findByUserIdOrderByDateDesc(Long userId);

    List<Income> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    List<Income> findByUserIdAndCategoryIgnoreCaseOrderByDateDesc(Long userId, String category);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.id = :userId AND i.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalIncomeBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.user.id = :userId")
    BigDecimal getLifetimeTotalIncome(@Param("userId") Long userId);

    @Query("SELECT i.category, SUM(i.amount), COUNT(i) FROM Income i WHERE i.user.id = :userId AND i.date BETWEEN :startDate AND :endDate GROUP BY i.category ORDER BY SUM(i.amount) DESC")
    List<Object[]> getCategoryStatsBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Fallbacks when userId is null
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalIncomeBetweenAll(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i")
    BigDecimal getLifetimeTotalIncomeAll();
}
