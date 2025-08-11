package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    /** Finds an expense by ID for a specific user. */
    Optional<Expense> findByIdAndUser_Id(Long id, Long userId);

    /** Lists all expenses for a specific user. */
    List<Expense> findAllByUser_Id(Long userId);

    /**
     * Searches expenses for a user with optional filters:
     * category, date range, amount range, and text in description.
     */
    @Query("""
        SELECT e FROM Expense e
        WHERE e.user.id = :userId
          AND (:category IS NULL OR e.category = :category)
          AND (:start IS NULL OR e.dateOfExpense >= :start)
          AND (:end IS NULL OR e.dateOfExpense <= :end)
          AND (:minAmount IS NULL OR e.amount >= :minAmount)
          AND (:maxAmount IS NULL OR e.amount <= :maxAmount)
          AND (:q IS NULL OR LOWER(COALESCE(e.description, '')) LIKE LOWER(CONCAT('%', :q, '%')))
        ORDER BY e.dateOfExpense DESC, e.id DESC
        """)
    List<Expense> searchExpenses(
            @Param("userId") Long userId,
            @Param("category") ExpenseCategory category,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("q") String q
    );
}
