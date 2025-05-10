package org.example.expensemanager;

import java.time.LocalDateTime;

public class Expense {
    private long id;
    private double amount;
    private String description;
    private String category;
    private LocalDateTime dateTime;
    private ExpenseStatus status;

    public Expense() {
        // Для Gson
    }

    public Expense(double amount, String description, String category, LocalDateTime dateTime) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.dateTime = dateTime;
        this.updateStatus();
    }

    // Расчет статуса
    public void updateStatus() {
        if (this.amount > 10000) {
            this.status = ExpenseStatus.LARGE_EXPENSE;
        } else if (this.amount < 1000) {
            this.status = ExpenseStatus.SMALL_EXPENSE;
        } else {
            this.status = ExpenseStatus.REGULAR_EXPENSE;
        }
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        this.amount = amount;
        updateStatus();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    public ExpenseStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", dateTime=" + dateTime +
                ", status=" + status.getDisplayName() +
                '}';
    }
}