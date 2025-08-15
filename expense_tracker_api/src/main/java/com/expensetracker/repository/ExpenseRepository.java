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


public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    /** Finds an expense by ID for a specific user. */
    Optional<Expense> findByIdAndUser_Id(Long id, Long userId);

    /** Lists all expenses for a specific user. */
    List<Expense> findAllByUser_Id(Long userId);

    /**
     * Searches expenses for a user with optional filters:
     * category, date range, amount range, and text in description.
     *//*
    @Query("""
      SELECT e FROM Expense e
      WHERE e.user.id = :userId
        AND e.active = true
        
        AND (COALESCE(:category, e.category) = e.category)
        
        AND (e.dateOfExpense >= COALESCE(:start, e.dateOfExpense))
        AND (e.dateOfExpense <= COALESCE(:end,   e.dateOfExpense))
        
        AND (e.amount >= COALESCE(:minAmount, e.amount))
        AND (e.amount <= COALESCE(:maxAmount, e.amount))

        AND (:qLower IS NULL OR LOWER(COALESCE(e.description, '')) LIKE CONCAT('%', :qLower, '%'))
      ORDER BY e.dateOfExpense DESC, e.id DESC
    """)
    List<Expense> searchByFilters(
            @Param("userId") Long userId,
            @Param("category") ExpenseCategory category,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("qLower") String qLower
    );
*/
}
