package org.example.expensemanager.business;

import org.example.expensemanager.business.model.Expense;
import org.example.expensemanager.data.ExpenseDao;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

public class BudgetForecast {
    private final ExpenseDao expenseDao;

    public BudgetForecast(ExpenseDao expenseDao) {
        this.expenseDao = expenseDao;
    }

    public double calculateMonthlyAverage() {
        LocalDate now = LocalDate.now();
        LocalDate threeMonthsAgo = now.minus(3, ChronoUnit.MONTHS);

        return expenseDao.getAllExpenses().stream()
                .filter(e -> !e.getDateTime().toLocalDate().isBefore(threeMonthsAgo))
                .mapToDouble(Expense::getAmount)
                .average()
                .orElse(0);
    }

    public double predictNextMonthExpenses() {
        double average = calculateMonthlyAverage();
        Month nextMonth = LocalDate.now().getMonth().plus(1);

        // Учет сезонности
        if (nextMonth == Month.DECEMBER || nextMonth == Month.JANUARY) {
            return average * 1.3; // 30% в праздники
        } else if (nextMonth == Month.JULY || nextMonth == Month.AUGUST) {
            return average * 1.2; // 20% летом
        }

        return average;
    }
}
