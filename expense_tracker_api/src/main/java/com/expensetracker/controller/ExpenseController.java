package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.mapper.ExpenseMapper;
import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenseEntities = expenseService.getAllExpenses();
        return expenseEntities.stream()
                .map(ExpenseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ExpenseResponse getExpenseById(@RequestParam Long id) {
        Expense expenseEntity = expenseService.getExpenseById(id);
        return ExpenseMapper.toResponse(expenseEntity);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        Expense expenseToSave = ExpenseMapper.toEntity(expenseRequest);
        Expense savedExpense = expenseService.createExpense(expenseToSave);
        ExpenseResponse expenseResponse = ExpenseMapper.toResponse(savedExpense);
        return ResponseEntity.ok(expenseResponse);
    }

    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(@RequestParam Long id, @Valid @RequestBody ExpenseRequest expenseRequest) {
        Expense existingExpense = expenseService.getExpenseById(id);
        ExpenseMapper.copyToEntity(existingExpense, expenseRequest);
        Expense updatedExpense = expenseService.createExpense(existingExpense); // reuse save
        return ExpenseMapper.toResponse(updatedExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@RequestParam Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<ExpenseResponse> searchExpenses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return expenseService.searchExpenses(q, category, minAmount, maxAmount, startDate, endDate);
    }
}
