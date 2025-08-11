package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.mapper.ExpenseMapper;
import com.expensetracker.model.Expense;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.model.ExpenseCategory;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserService userService;


    public ExpenseService(ExpenseRepository expenseRepository, UserService userService) {
        this.expenseRepository = expenseRepository;
        this.userService = userService;
    }

    /** Lists all active expenses owned by the current user. */
    public List<ExpenseResponse> listAllExpensesByUser() {
        User current = userService.getCurrentUserOrThrow();
        return expenseRepository.findAllByUser_Id(current.getId())
                .stream()
                .map(ExpenseMapper::toResponse)
                .toList();
    }

    /** Creates a new expense for the current user. */
    public ExpenseResponse createExpense(ExpenseRequest request) {
        User current = userService.getCurrentUserOrThrow();
        Expense expense = ExpenseMapper.toEntity(request, current);
        Expense saved = expenseRepository.save(expense);
        return ExpenseMapper.toResponse(saved);
    }

    public ExpenseResponse getExpenseById(Long id) {
        User current = userService.getCurrentUserOrThrow();
        Expense expense = expenseRepository.findByIdAndUser_Id(id, current.getId())
                .orElseThrow(() -> new BadRequestException("Expense not found."));
        return ExpenseMapper.toResponse(expense);
    }

    /** Updates an existing expense if it belongs to the current user. */
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        User current = userService.getCurrentUserOrThrow();
        Expense expense = expenseRepository.findByIdAndUser_Id(id, current.getId())
                .orElseThrow(() -> new BadRequestException("Expense not found."));

        // Apply allowed changes
        ExpenseMapper.copyToEntity(expense, request);

        Expense saved = expenseRepository.save(expense);
        return ExpenseMapper.toResponse(saved);
    }

    /** Soft-deletes an expense (active=false) if owned by the current user. */
    public void deleteExpense(Long id) {
        User current = userService.getCurrentUserOrThrow();
        Expense expense = expenseRepository.findByIdAndUser_Id(id, current.getId())
                .orElseThrow(() -> new BadRequestException("Expense not found."));
        expense.setActive(false);
        expenseRepository.save(expense);
    }
    /**
     * Flexible search over current user's expenses (all filters optional).
     * Sort order is handled in the repository query.
     */
    public List<ExpenseResponse> searchExpense(
            String category, LocalDate start, LocalDate end,
            BigDecimal minAmount, BigDecimal maxAmount, String q) {

        User current = userService.getCurrentUserOrThrow();

        ExpenseCategory catEnum = null;
        if (category != null && !category.isBlank()) {
            try {
                catEnum = ExpenseCategory.valueOf(category.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid category: " + category);
            }
        }

        return expenseRepository.searchExpenses(
                        current.getId(), catEnum, start, end, minAmount, maxAmount,
                        (q == null || q.isBlank()) ? null : q.trim())
                .stream()
                .map(ExpenseMapper::toResponse)
                .toList();
    }

}
