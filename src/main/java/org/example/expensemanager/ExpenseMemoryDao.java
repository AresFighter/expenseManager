package org.example.expensemanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ExpenseMemoryDao implements ExpenseDao {
    private final List<Expense> expenses = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void addExpense(Expense expense) {
        expense.setId(idGenerator.getAndIncrement());
        expenses.add(expense);
    }

    @Override
    public void updateExpense(Expense expense) {
        for (int i = 0; i < expenses.size(); i++) {
            if (expenses.get(i).getId() == expense.getId()) {
                expenses.set(i, expense);
                return;
            }
        }
    }

    @Override
    public void deleteExpense(long id) {
        expenses.removeIf(e -> e.getId() == id);
    }

    @Override
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }
}