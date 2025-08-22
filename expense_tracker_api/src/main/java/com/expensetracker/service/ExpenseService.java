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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

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
    @Transactional(readOnly = true)
    public List<ExpenseResponse> searchExpenses(
            String category, LocalDate start, LocalDate end,
            BigDecimal minAmount, BigDecimal maxAmount, String q) {

        ensureValid(start, end, minAmount, maxAmount);
        Long userId = userService.getCurrentUserOrThrow().getId();
        Specification<Expense> spec = buildSpec(userId, category, start, end, minAmount, maxAmount, q);

        var sort = Sort.by(Sort.Order.desc("dateOfExpense"), Sort.Order.desc("id"));
        return expenseRepository.findAll(spec, sort)
                .stream().map(ExpenseMapper::toResponse).toList();
    }



    private Specification<Expense> buildSpec(
            Long userId, String category, LocalDate start, LocalDate end,
            BigDecimal minAmount, BigDecimal maxAmount, String q) {

        ExpenseCategory cat = parseCategory(category);
        String qLower = normalize(q);

        Specification<Expense> spec = (root, query, cb) ->
                cb.and(
                        cb.equal(root.get("user").get("id"), userId),
                        cb.isTrue(root.get("active"))
                );

        if (cat != null)       spec = spec.and((r,qy,cb) -> cb.equal(r.get("category"), cat));
        if (start != null)     spec = spec.and((r,qy,cb) -> cb.greaterThanOrEqualTo(r.get("dateOfExpense"), start));
        if (end != null)       spec = spec.and((r,qy,cb) -> cb.lessThanOrEqualTo(r.get("dateOfExpense"), end));
        if (minAmount != null) spec = spec.and((r,qy,cb) -> cb.greaterThanOrEqualTo(r.get("amount"), minAmount));
        if (maxAmount != null) spec = spec.and((r,qy,cb) -> cb.lessThanOrEqualTo(r.get("amount"), maxAmount));
        if (qLower != null)    spec = spec.and((r,qy,cb) ->
                cb.like(cb.lower(cb.coalesce(r.get("description"), "")), "%" + qLower + "%"));

        return spec;
    }

    private ExpenseCategory parseCategory(String c) {
        if (c == null || c.isBlank()) return null;
        try { return ExpenseCategory.valueOf(c.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { throw new BadRequestException("Invalid category: " + c); }
    }

    private void ensureValid(LocalDate start, LocalDate end, BigDecimal min, BigDecimal max) {
        if (start != null && end != null && start.isAfter(end))
            throw new BadRequestException("start must be <= end");
        if (min != null && max != null && min.compareTo(max) > 0)
            throw new BadRequestException("minAmount must be <= maxAmount");
    }

    private String normalize(String s) {
        return (s == null || s.isBlank()) ? null : s.trim().toLowerCase();
    }

}
