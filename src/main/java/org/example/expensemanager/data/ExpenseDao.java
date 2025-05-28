package org.example.expensemanager.data;

import org.example.expensemanager.business.model.Expense;

import java.util.List;

public interface ExpenseDao {
    void addExpense(Expense expense);
    void updateExpense(Expense expense);
    void deleteExpense(long id);
    List<Expense> getAllExpenses();
}