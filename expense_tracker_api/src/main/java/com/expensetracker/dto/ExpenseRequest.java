package com.expensetracker.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class ExpenseRequest {
    @NotBlank @Size(max = 120)
    private String title;

    @NotNull @Positive
    private Double amount;

    @NotBlank @Size(max = 60)
    private String category;

    @NotNull @PastOrPresent
    private LocalDate date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
