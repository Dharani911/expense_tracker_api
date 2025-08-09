package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


public interface ExpenseRepository
        extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    @Query("""
        SELECT e FROM Expense e
        WHERE (:q IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:category IS NULL OR e.category = :category)
          AND (:minAmount IS NULL OR e.amount >= :minAmount)
          AND (:maxAmount IS NULL OR e.amount <= :maxAmount)
          AND (:startDate IS NULL OR e.date >= :startDate)
          AND (:endDate IS NULL OR e.date <= :endDate)
        """)
    List<Expense> search(
            @Param("q") String query,
            @Param("category") String category,
            @Param("minAmount") Double minAmount,
            @Param("maxAmount") Double maxAmount,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
