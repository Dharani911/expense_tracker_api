package com.expensetracker.mapper;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.model.Expense;

public final class ExpenseMapper {

    private ExpenseMapper() {}

    public static Expense toEntity(ExpenseRequest expenseRequest) {
        Expense expenseEntity = new Expense();
        expenseEntity.setTitle(expenseRequest.getTitle());
        expenseEntity.setAmount(expenseRequest.getAmount());
        expenseEntity.setCategory(expenseRequest.getCategory());
        expenseEntity.setDate(expenseRequest.getDate());
        return expenseEntity;
    }

    public static void copyToEntity(Expense existingExpense, ExpenseRequest expenseRequest) {
        existingExpense.setTitle(expenseRequest.getTitle());
        existingExpense.setAmount(expenseRequest.getAmount());
        existingExpense.setCategory(expenseRequest.getCategory());
        existingExpense.setDate(expenseRequest.getDate());
    }

    public static ExpenseResponse toResponse(Expense expenseEntity) {
        ExpenseResponse expenseResponse = new ExpenseResponse();
        expenseResponse.setId(expenseEntity.getId());
        expenseResponse.setTitle(expenseEntity.getTitle());
        expenseResponse.setAmount(expenseEntity.getAmount());
        expenseResponse.setCategory(expenseEntity.getCategory());
        expenseResponse.setDate(expenseEntity.getDate());
        return expenseResponse;
    }
}
