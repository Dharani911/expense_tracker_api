package com.expensetracker.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "expenses")

public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 120)
    private String title;

    @NotNull @Positive
    private Double amount;

    @NotBlank @Size(max = 60)
    private String category;

    @NotNull @PastOrPresent
    private LocalDate date;

    public Expense() {
    }

    public Expense(Long id, String title, Double amount, String category, LocalDate date) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense expense)) return false;
        return Objects.equals(getId(), expense.getId()) && Objects.equals(getTitle(), expense.getTitle()) && Objects.equals(getAmount(), expense.getAmount()) && Objects.equals(getCategory(), expense.getCategory()) && Objects.equals(getDate(), expense.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getAmount(), getCategory(), getDate());
    }

}

