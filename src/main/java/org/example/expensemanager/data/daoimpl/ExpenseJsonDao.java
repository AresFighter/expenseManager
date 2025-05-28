package org.example.expensemanager.data.daoimpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.expensemanager.business.model.Expense;
import org.example.expensemanager.data.ExpenseDao;
import org.example.expensemanager.data.LocalDateTimeAdapter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ExpenseJsonDao implements ExpenseDao {
    private final String filePath;
    private final Gson gson;
    private final AtomicLong idGenerator = new AtomicLong(1);

    public ExpenseJsonDao(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        initializeFile();
    }

    private void initializeFile() {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            try {
                Files.write(Paths.get(filePath), "[]".getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Не удалось создать JSON файл", e);
            }
        }
    }

    @Override
    public void addExpense(Expense expense) {
        List<Expense> expenses = getAllExpenses();
        expense.setId(idGenerator.getAndIncrement());
        expenses.add(expense);
        saveAll(expenses);
    }

    @Override
    public void updateExpense(Expense expense) {
        List<Expense> expenses = getAllExpenses();
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == expense.getId()) {
                expenses.set(i, expense);
                saveAll(expenses);
                return;
            }
        }
        throw new RuntimeException("Расход не найден по id: " + expense.getId());
    }

    @Override
    public void deleteExpense(long id) {
        List<Expense> expenses = getAllExpenses();
        expenses.removeIf(e -> e.getId() == id);
        saveAll(expenses);
    }

    @Override
    public List<Expense> getAllExpenses() {
        try (Reader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<Expense>>() {}.getType();
            List<Expense> expenses = gson.fromJson(reader, listType);

            if (expenses == null) {
                expenses = new ArrayList<>();
            }

            // Обновляем статусы для всех расходов
            expenses.forEach(expense -> {
                if (expense.getStatus() == null && expense.getAmount() != 0) {
                    expense.updateStatus();
                }
            });

            long maxId = expenses.stream().mapToLong(Expense::getId).max().orElse(0);
            idGenerator.set(maxId + 1);

            return expenses;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения JSON", e);
        }
    }


    private void saveAll(List<Expense> expenses) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(expenses, writer);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи JSON", e);
        }
    }
}
