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

import java.math.BigDecimal;
import java.net.URI;
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

    @GetMapping("/list")
    public ResponseEntity<List<ExpenseResponse>> listAllExpensesByUser() {
        return ResponseEntity.ok(expenseService.listAllExpensesByUser());
    }

    /** Returns a single expense by id (owned by current user). */
    @GetMapping("/get")
    public ResponseEntity<ExpenseResponse> getById(@RequestParam Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest expenseRequest) {
        ExpenseResponse created = expenseService.createExpense(expenseRequest);
        return ResponseEntity
                .created(URI.create("/api/expenses/" + created.getId()))
                .body(created);
    }

    @PostMapping("/update")
    public ResponseEntity<ExpenseResponse> updateExpense(@RequestParam Long id, @Valid @RequestBody ExpenseRequest expenseRequest) {
        return ResponseEntity.ok(expenseService.updateExpense(id, expenseRequest));
    }

    /** Soft-deletes an expense . */
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches current user's expenses with optional filters.
     * Example: /api/expenses/search?category=FOOD&start=2025-01-01&minAmount=10&q=lunch
     */
    @GetMapping("/search")
    public ResponseEntity<List<ExpenseResponse>> searchV2(
            @RequestParam(required = false) String category,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false, name = "q") String q
    ) {
        return ResponseEntity.ok(
                expenseService.searchExpenses(category, start, end, minAmount, maxAmount, q)
        );
    }

}
