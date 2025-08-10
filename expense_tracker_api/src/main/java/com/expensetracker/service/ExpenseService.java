package com.expensetracker.service;

import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.mapper.ExpenseMapper;
import com.expensetracker.model.Expense;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
    }

    @Transactional
    public Expense updateExpense(Long id, Expense expenseDetails) {
        Expense expense = getExpenseById(id);
        if (expense != null) {
            expense.setAmount(expenseDetails.getAmount());
            expense.setCategory(expenseDetails.getCategory());
            expense.setDescription(expenseDetails.getDescription());
            expense.setDateOfExpense(expenseDetails.getDateOfExpense());
            return expenseRepository.save(expense);
        }
        return null;
    }

    public void deleteExpense(Long id) {

        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found: " + id);
        }
        expenseRepository.deleteById(id);
    }

}
