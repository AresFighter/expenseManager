package org.example.expensemanager.business.model;

import java.time.LocalDateTime;

/**
 * Класс представляет информацию о единичной трате пользователя.
 * Содержит сумму, описание, категорию, дату и статус расхода.
 *
 * <p>Статус автоматически рассчитывается на основе суммы:</p>
 * <ul>
 *     <li>до 1000 — {@link ExpenseStatus#SMALL_EXPENSE}</li>
 *     <li>от 1000 до 10000 — {@link ExpenseStatus#REGULAR_EXPENSE}</li>
 *     <li>свыше 10000 — {@link ExpenseStatus#LARGE_EXPENSE}</li>
 * </ul>
 *
 * @author —
 */
public class Expense {

    /** Уникальный идентификатор расхода. */
    private long id;

    /** Сумма расхода. */
    private double amount;

    /** Описание расхода. */
    private String description;

    /** Категория расхода (например, "Продукты", "Транспорт"). */
    private String category;

    /** Дата и время совершения расхода. */
    private LocalDateTime dateTime;

    /** Автоматически определяемый статус расхода. */
    private ExpenseStatus status;

    /**
     * Конструктор по умолчанию (для сериализации).
     */
    public Expense() {
        // Используется GSON
    }

    /**
     * Конструктор с параметрами.
     *
     * @param amount      сумма расхода
     * @param description описание
     * @param category    категория
     * @param dateTime    дата и время
     */
    public Expense(double amount, String description, String category, LocalDateTime dateTime) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.dateTime = dateTime;
        this.updateStatus();
    }

    /**
     * Обновляет статус расхода на основе текущей суммы.
     */
    public void updateStatus() {
        if (this.amount > 10000) {
            this.status = ExpenseStatus.LARGE_EXPENSE;
        } else if (this.amount < 1000) {
            this.status = ExpenseStatus.SMALL_EXPENSE;
        } else {
            this.status = ExpenseStatus.REGULAR_EXPENSE;
        }
    }

    // Геттеры и сеттеры

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
