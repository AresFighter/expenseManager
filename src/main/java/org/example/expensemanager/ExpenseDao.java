package org.example.expensemanager;

import java.util.List;

public interface ExpenseDao {
    void addExpense(Expense expense);
    void updateExpense(Expense expense);
    void deleteExpense(long id);
    List<Expense> getAllExpenses();
}