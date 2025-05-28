package core.model;

/**
 * Перечисление возможных статусов расхода.
 * Определяется автоматически на основе суммы.
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
