package org.example.expensemanager.service;

import org.example.expensemanager.business.ExpenseCategoryManager;
import org.example.expensemanager.data.ExpenseDao;
import org.example.expensemanager.business.model.Expense;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class ExpenseService {
    private final ExpenseDao dao;
    private final ExpenseCategoryManager categoryManager;

    public ExpenseService(ExpenseDao dao, ExpenseCategoryManager categoryManager) {
        this.dao = dao;
        this.categoryManager = categoryManager;
    }

    public List<Expense> getAll() {
        return dao.getAllExpenses();
    }

    public void add(Expense expense) {
        // Проверка дубликатов
        for (Expense existing : dao.getAllExpenses()) {
            boolean sameAmount = existing.getAmount() == expense.getAmount();
            boolean sameDesc = existing.getDescription().equalsIgnoreCase(expense.getDescription());
            long minutes = ChronoUnit.MINUTES.between(existing.getDateTime(), expense.getDateTime());
            if (sameAmount && sameDesc && Math.abs(minutes) <= 60) {
                throw new IllegalArgumentException("Дубликат расхода найден в пределах 1 часа");
            }
        }

        // Категоризация
        if (expense.getCategory() == null || expense.getCategory().isEmpty()) {
            String autoCategory = categoryManager.determineCategory(expense.getDescription(), expense.getAmount());
            expense.setCategory(autoCategory);
        }

        dao.addExpense(expense);
    }

    public void update(Expense expense) {
        dao.updateExpense(expense);
    }

    public void delete(long id) {
        dao.deleteExpense(id);
    }
}
