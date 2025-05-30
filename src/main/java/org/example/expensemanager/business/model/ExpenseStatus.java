package org.example.expensemanager.business.model;

/**
 * Перечисление статусов расхода на основе суммы.
 * <p>
 * Используется в классе {@link Expense} для автоматической классификации трат.
 */
public enum ExpenseStatus {

    /** Мелкий расход (менее 1000). */
    SMALL_EXPENSE("Мелкий расход"),

    /** Обычный расход (от 1000 до 10000). */
    REGULAR_EXPENSE("Обычный расход"),

    /** Крупная покупка (свыше 10000). */
    LARGE_EXPENSE("Крупная покупка");

    /** Человеко-читаемое название статуса. */
    private final String displayName;

    /**
     * Создает статус с указанным названием.
     * @param displayName название для отображения (не может быть null)
     */
    ExpenseStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает текстовое представление статуса.
     *
     * @return строка для отображения пользователю
     */
    public String getDisplayName() {
        return displayName;
    }
}
