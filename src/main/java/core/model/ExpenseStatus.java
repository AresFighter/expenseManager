package core.model;

public enum ExpenseStatus {
    SMALL_EXPENSE("Мелкий расход"),
    REGULAR_EXPENSE("Обычный расход"),
    LARGE_EXPENSE("Крупная покупка");

    private final String displayName;

    ExpenseStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}