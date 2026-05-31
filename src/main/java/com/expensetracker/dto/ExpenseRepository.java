package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import com.expensetracker.enums.Category;
import com.expensetracker.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Basic user-scoped find
    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    // Paginated list for user
    Page<Expense> findByUserId(Long userId, Pageable pageable);

    // Full-text search with filters
    @Query("""
        SELECT e FROM Expense e
        WHERE e.user.id = :userId
          AND (:keyword IS NULL OR :keyword = '' OR
               LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:category IS NULL OR e.category = :category)
          AND (:paymentMethod IS NULL OR e.paymentMethod = :paymentMethod)
          AND (:startDate IS NULL OR e.expenseDate >= :startDate)
          AND (:endDate IS NULL OR e.expenseDate <= :endDate)
          AND (:minAmount IS NULL OR e.amount >= :minAmount)
          AND (:maxAmount IS NULL OR e.amount <= :maxAmount)
        """)
    Page<Expense> findWithFilters(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            Pageable pageable
    );

    // Dashboard: total expenses sum
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalExpensesByUserId(@Param("userId") Long userId);

    // Dashboard: monthly total
    @Query("""
        SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
        WHERE e.user.id = :userId
          AND MONTH(e.expenseDate) = :month
          AND YEAR(e.expenseDate) = :year
        """)
    BigDecimal getMonthlyExpenses(
            @Param("userId") Long userId,
            @Param("month") int month,
            @Param("year") int year
    );

    // Dashboard: category-wise totals
    @Query("""
        SELECT e.category, COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
        GROUP BY e.category
        ORDER BY SUM(e.amount) DESC
        """)
    List<Object[]> getCategoryWiseExpenses(@Param("userId") Long userId);

    // Dashboard: recent 5 expenses
    @Query("""
        SELECT e FROM Expense e
        WHERE e.user.id = :userId
        ORDER BY e.expenseDate DESC, e.createdAt DESC
        """)
    List<Expense> findRecentExpenses(@Param("userId") Long userId, Pageable pageable);

    // Count by user
    long countByUserId(Long userId);

    // Monthly breakdown for chart
    @Query("""
        SELECT MONTH(e.expenseDate), COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId AND YEAR(e.expenseDate) = :year
        GROUP BY MONTH(e.expenseDate)
        ORDER BY MONTH(e.expenseDate)
        """)
    List<Object[]> getMonthlyBreakdown(@Param("userId") Long userId, @Param("year") int year);
}