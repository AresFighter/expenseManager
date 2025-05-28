package org.example.expensemanager.data.daoimpl;

import org.example.expensemanager.business.model.Expense;
import org.example.expensemanager.data.ExpenseDao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExpensePostgresDao implements ExpenseDao {
    private final Connection connection;

    public ExpensePostgresDao(String url, String user, String password) {
        try {
            connection = DriverManager.getConnection(url, user, password);
            createTableIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных PostgreSQL", e);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS expenses (" +
                "id SERIAL PRIMARY KEY, " +
                "amount DECIMAL(10,2) NOT NULL, " +
                "description VARCHAR(255) NOT NULL, " +
                "category VARCHAR(50) NOT NULL, " +
                "date_time TIMESTAMP NOT NULL" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public void addExpense(Expense expense) {
        String sql = "INSERT INTO expenses (amount, description, category, date_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, expense.getAmount());
            stmt.setString(2, expense.getDescription());
            stmt.setString(3, expense.getCategory());
            stmt.setTimestamp(4, Timestamp.valueOf(expense.getDateTime()));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    expense.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка добавления расхода", e);
        }
    }

    @Override
    public void updateExpense(Expense expense) {
        String sql = "UPDATE expenses SET amount=?, description=?, category=?, date_time=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, expense.getAmount());
            stmt.setString(2, expense.getDescription());
            stmt.setString(3, expense.getCategory());
            stmt.setTimestamp(4, Timestamp.valueOf(expense.getDateTime()));
            stmt.setLong(5, expense.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка обновления расхода", e);
        }
    }

    @Override
    public void deleteExpense(long id) {
        String sql = "DELETE FROM expenses WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка удаления расхода", e);
        }
    }

    @Override
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Expense expense = new Expense();
                expense.setId(rs.getInt("id"));
                expense.setAmount(rs.getDouble("amount"));
                expense.setDescription(rs.getString("description"));
                expense.setCategory(rs.getString("category"));

                Timestamp ts = rs.getTimestamp("date_time");
                if (ts != null) {
                    expense.setDateTime(ts.toLocalDateTime());
                } else {
                    // Временно подставляем текущую дату
                    expense.setDateTime(LocalDateTime.now());
                }

                expenses.add(expense);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения расходов", e);
        }

        return expenses;
    }
}
