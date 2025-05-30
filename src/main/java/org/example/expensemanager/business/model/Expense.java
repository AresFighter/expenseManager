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
 * @author — AresFighter
 */
public class Expense {

    /** Уникальный идентификатор расхода. */
    private long id;

    /**
     * Сумма расхода. При изменении автоматически обновляет статус.
     * @see #updateStatus()
     */
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
     * Вызывается автоматически при изменении {@link #amount}.
     * <p>
     * Логика расчета:
     * <ul>
     *     <li>Если сумма > 10000 → {@link ExpenseStatus#LARGE_EXPENSE}</li>
     *     <li>Если сумма < 1000 → {@link ExpenseStatus#SMALL_EXPENSE}</li>
     *     <li>Иначе → {@link ExpenseStatus#REGULAR_EXPENSE}</li>
     * </ul>
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

    /**
     * Возвращает идентификатор расхода.
     * @return уникальный числовой идентификатор
     */
    public long getId() { return id; }

    /**
     * Устанавливает идентификатор расхода.
     * @param id уникальный числовой идентификатор
     */
    public void setId(long id) { this.id = id; }

    /**
     * Возвращает сумму расхода.
     * @return сумма в рублях (или другой валюте)
     */
    public double getAmount() { return amount; }

    /**
     * Устанавливает новую сумму. Автоматически обновляет статус.
     * @param amount новая сумма (должна быть положительной)
     * @throws IllegalArgumentException если сумма отрицательная
     */
    public void setAmount(double amount) {
        this.amount = amount;
        updateStatus();
    }

    /**
     * Возвращает описание расхода.
     * @return текстовое описание траты
     */
    public String getDescription() { return description; }

    /**
     * Устанавливает описание расхода.
     * @param description текстовое описание (не может быть null или пустым)
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Возвращает категорию расхода.
     * @return название категории (например, "Продукты")
     */
    public String getCategory() { return category; }

    /**
     * Устанавливает категорию расхода.
     * @param category название категории (если null, будет определена автоматически)
     */
    public void setCategory(String category) { this.category = category; }

    /**
     * Возвращает дату и время расхода.
     * @return объект LocalDateTime
     */
    public LocalDateTime getDateTime() { return dateTime; }

    /**
     * Устанавливает дату и время расхода.
     * @param dateTime дата и время (не может быть null)
     */
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }

    /**
     * Возвращает статус расхода.
     * @return enum-значение ExpenseStatus
     */
    public ExpenseStatus getStatus() { return status; }

    /**
     * Возвращает строковое представление расхода.
     * @return строка в формате: "Expense{id=1, amount=1500, description='АЗС', ...}"
     */
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
