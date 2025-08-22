package com.expensetracker.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseRequest {

    @NotBlank
    @Size(max = 50) // matches VARCHAR(50)
    private String category;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be > 0")
    @Digits(integer = 10, fraction = 2) // matches NUMERIC(10,2)
    private BigDecimal amount;

    @Size(max = 1000)
    private String description;

    @NotNull
    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDate dateOfExpense;

    // getters/setters

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfExpense() {
        return dateOfExpense;
    }

    public void setDateOfExpense(LocalDate dateOfExpense) {
        this.dateOfExpense = dateOfExpense;
    }
}
