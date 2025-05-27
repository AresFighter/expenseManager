package core.dao;

import io.github.cdimascio.dotenv.Dotenv;

public class ExpenseDaoFactory {
    private final Dotenv dotenv;

    public ExpenseDaoFactory() {
        this.dotenv = Dotenv.configure()
                .filename("config.env")
                .load();
    }

    public ExpenseDao createMemoryDao() {
        return new ExpenseMemoryDao();
    }

    public ExpenseDao createPostgresDao() {
        try {
            String url = dotenv.get("DB_URL");
            String user = dotenv.get("DB_USER");
            String password = dotenv.get("DB_PASSWORD");

            if (url == null || user == null || password == null) {
                throw new RuntimeException("В env-файле отсутствует конфигурация БД");
            }

            return new ExpensePostgresDao(url, user, password);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать PostgreSQL DAO: " + e.getMessage(), e);
        }
    }

    public ExpenseDao createJsonDao() {
        String filePath = dotenv.get("JSON_FILE_PATH", "expenses.json");
        return new ExpenseJsonDao(filePath);
    }
}