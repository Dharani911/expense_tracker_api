package com.expensetracker.mapper;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.model.Expense;
import com.expensetracker.model.ExpenseCategory;
import com.expensetracker.model.User;
import com.expensetracker.util.*;


public final class ExpenseMapper {

    private ExpenseMapper() {}

    private static String nullIfBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static Expense toEntity(ExpenseRequest request, User user) {
        Expense expense = new Expense();
        expense.setCategory(EnumUtil.toEnum(request.getCategory(), ExpenseCategory.class));
        expense.setAmount(request.getAmount());
        expense.setDescription(nullIfBlank(request.getDescription()));
        expense.setDateOfExpense(request.getDateOfExpense());
        expense.setActive(true);
        expense.setUser(user);
        return expense;
    }

    public static void copyToEntity(Expense target, ExpenseRequest req) {
        if (target == null || req == null) return;

        FunctionUtil.setIfPresent(req.getAmount(), target::setAmount);
        FunctionUtil.setIfPresent(EnumUtil.toEnum(req.getCategory(), ExpenseCategory.class), target::setCategory);
        FunctionUtil.setIfPresent(nullIfBlank(req.getDescription()), target::setDescription);
        FunctionUtil.setIfPresent(req.getDateOfExpense(), target::setDateOfExpense);
    }

    public static ExpenseResponse toResponse(Expense expense) {
        if (expense == null) return null;

        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setCategory(EnumUtil.toString(expense.getCategory()));
        response.setAmount(expense.getAmount());
        response.setDescription(expense.getDescription());
        response.setDateOfExpense(expense.getDateOfExpense());
        response.setActive(Boolean.TRUE.equals(expense.getActive()));
        response.setUserId(expense.getUser() != null ? expense.getUser().getId() : null);
        return response;
    }
}
