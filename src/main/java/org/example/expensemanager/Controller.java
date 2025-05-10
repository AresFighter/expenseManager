package org.example.expensemanager;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML private TableView<Expense> expenseTable;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private ChoiceBox<String> categoryChoiceBox;
    @FXML private ChoiceBox<String> dataSourceChoiceBox;
    @FXML private Button selectDataSourceButton;
    @FXML private TextField filterField;
    @FXML private ChoiceBox<String> sortChoiceBox;
    @FXML private LineChart<String, Number> forecastChart;
    @FXML private Spinner<LocalTime> timeSpinner;

    private ExpenseDao expenseDao;
    private ExpenseDao memoryDao;
    private ExpenseDao postgresDao;
    private ExpenseDao jsonDao;
    private ExpenseCategoryManager categoryManager;
    private ExpenseDaoFactory daoFactory;


    @FXML
    public void initialize() {
        // Инициализация фабрики и менеджера категорий
        daoFactory = new ExpenseDaoFactory();
        categoryManager = new ExpenseCategoryManager("categories.json");

        // Источники
        memoryDao = daoFactory.createMemoryDao();
        postgresDao = daoFactory.createPostgresDao();
        jsonDao = daoFactory.createJsonDao();
        expenseDao = memoryDao;

        categoryChoiceBox.getItems().addAll(categoryManager.getAvailableCategories());

        dataSourceChoiceBox.getItems().addAll("Память", "PostgreSQL", "JSON");
        dataSourceChoiceBox.setValue("Память");

        sortChoiceBox.getItems().addAll(
                "По дате (новые)", "По дате (старые)",
                "По сумме (возрастание)", "По сумме (убывание)"
        );
        sortChoiceBox.setValue("По дате (новые)");

        setupTableColumns();
        datePicker.setValue(LocalDate.now()); // текущая дата
        setupTimeSpinner(); // текущая время (часы и минуты)
        setupEventHandlers();
        updateTableView(); // загрузка данных
    }


    private void setupTimeSpinner() {
        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<>() {
            {
                setConverter(new StringConverter<>() {
                    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

                    @Override
                    public String toString(LocalTime time) {
                        return time != null ? formatter.format(time) : "";
                    }

                    @Override
                    public LocalTime fromString(String string) {
                        return LocalTime.parse(string, formatter);
                    }
                });
                setValue(LocalTime.now().withSecond(0).withNano(0));
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps));
            }
        };

        timeSpinner.setValueFactory(valueFactory);
        timeSpinner.setEditable(true);
    }

    private void setupTableColumns() {
        // Колонка для суммы
        TableColumn<Expense, Double> amountCol = new TableColumn<>("Сумма");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Колонка для описания
        TableColumn<Expense, String> descriptionCol = new TableColumn<>("Описание");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Колонка для категории
        TableColumn<Expense, String> categoryCol = new TableColumn<>("Категория");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Колонка для даты и времени
        TableColumn<Expense, String> dateCol = new TableColumn<>("Дата и время");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dateCol.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getDateTime();
            String formatted = (dt != null) ? dt.format(formatter) : "";
            return new SimpleStringProperty(formatted);
        });

        // Колонка для статуса
        TableColumn<Expense, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(cellData -> {
            Expense expense = cellData.getValue();
            ExpenseStatus status = expense.getStatus();
            String statusText = (status != null) ? status.getDisplayName() : "Не указан";
            return new SimpleStringProperty(statusText);
        });

        // Добавляем все колонки в таблицу
        expenseTable.getColumns().addAll(amountCol, descriptionCol, categoryCol, dateCol, statusCol);

        // Настройка изменения размера колонок
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupEventHandlers() {
        selectDataSourceButton.setOnAction(event -> {
            String source = dataSourceChoiceBox.getValue();
            switch (source) {
                case "Память":
                    expenseDao = memoryDao;
                    break;
                case "PostgreSQL":
                    expenseDao = postgresDao;
                    break;
                case "JSON":
                    expenseDao = jsonDao;
                    break;
            }
            updateTableView();
        });

        expenseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillFormWithSelectedExpense(newVal);
        });

        sortChoiceBox.setOnAction(e -> updateTableView());
        filterField.textProperty().addListener((obs, oldText, newText) -> updateTableView());
    }

    @FXML
    private void handleAddExpense() {
        try {
            if (!validateFields()) return;

            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionField.getText();
            String category = categoryChoiceBox.getValue();

            // Получаем дату и время из формы
            LocalDate selectedDate = datePicker.getValue();
            LocalTime selectedTime = timeSpinner.getValue();
            LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, selectedTime);

            // Автоматическое определение категории, если не выбрана
            if (category == null || category.isEmpty()) {
                category = categoryManager.determineCategory(description, amount);
            }

            // Проверка на дубликаты в пределах 1 часа
            for (Expense existing : expenseDao.getAllExpenses()) {
                boolean sameAmount = existing.getAmount() == amount;
                boolean sameDesc = existing.getDescription().equalsIgnoreCase(description);
                long minutes = ChronoUnit.MINUTES.between(existing.getDateTime(), selectedDateTime);
                if (sameAmount && sameDesc && Math.abs(minutes) <= 60) {
                    showAlert(Alert.AlertType.WARNING, "Дубликат",
                            "Платёж с такой суммой и описанием уже есть в течение 1 часа.");
                    return;
                }
            }

            Expense expense = new Expense(amount, description, category, selectedDateTime);
            expenseDao.addExpense(expense);
            updateTableView();
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось добавить расход: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Предупреждение", "Выберите расход для редактирования");
            return;
        }

        try {
            if (!validateFields()) return;

            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionField.getText();
            String category = categoryChoiceBox.getValue();

            LocalDate selectedDate = datePicker.getValue();
            LocalTime selectedTime = timeSpinner.getValue();
            LocalDateTime selectedDateTime = LocalDateTime.of(selectedDate, selectedTime);

            selected.setAmount(amount);
            selected.setDescription(description);
            selected.setCategory(category);
            selected.setDateTime(selectedDateTime);

            expenseDao.updateExpense(selected);
            updateTableView();
            clearFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Не удалось обновить расход: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "Предупреждение", "Выберите расход для удаления");
            return;
        }

        expenseDao.deleteExpense(selected.getId());
        updateTableView();
        clearFields();
    }

    @FXML
    private void handleBudgetForecast() {
        BudgetForecast forecast = new BudgetForecast(expenseDao);
        double monthlyAverage = forecast.calculateMonthlyAverage();
        double predicted = forecast.predictNextMonthExpenses();

        forecastChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Прогноз расходов");

        series.getData().add(new XYChart.Data<>("Среднее (3 мес)", monthlyAverage));
        series.getData().add(new XYChart.Data<>("Прогноз (след. мес)", predicted));

        forecastChart.getData().add(series);
    }


    private void updateTableView() {
        String filterText = filterField.getText().toLowerCase();
        String sortType = sortChoiceBox.getValue();
        List<Expense> allExpenses = expenseDao.getAllExpenses();
        List<Expense> filtered = new ArrayList<>();

        for (Expense e : allExpenses) {
            if (filterText.isEmpty() ||
                    e.getDescription().toLowerCase().contains(filterText) ||
                    e.getCategory().toLowerCase().contains(filterText) ||
                    String.valueOf(e.getAmount()).contains(filterText)) {
                filtered.add(e);
            }
        }

        switch (sortType) {
            case "По дате (новые)":
                filtered.sort((e1, e2) -> e2.getDateTime().compareTo(e1.getDateTime()));
                break;
            case "По дате (старые)":
                filtered.sort((e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()));
                break;
            case "По сумме (возрастание)":
                filtered.sort((e1, e2) -> Double.compare(e1.getAmount(), e2.getAmount()));
                break;
            case "По сумме (убывание)":
                filtered.sort((e1, e2) -> Double.compare(e2.getAmount(), e1.getAmount()));
                break;
        }

        expenseTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void fillFormWithSelectedExpense(Expense expense) {
        amountField.setText(String.valueOf(expense.getAmount()));
        descriptionField.setText(expense.getDescription());
        categoryChoiceBox.setValue(expense.getCategory());
        datePicker.setValue(expense.getDateTime().toLocalDate());
    }

    private void clearFields() {
        amountField.clear();
        descriptionField.clear();
        categoryChoiceBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        expenseTable.getSelectionModel().clearSelection();
    }

    private boolean validateFields() {
        if (amountField.getText().isEmpty() || descriptionField.getText().isEmpty()) {
            showAlert(AlertType.WARNING, "Предупреждение", "Заполните сумму и описание");
            return false;
        }

        try {
            Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Ошибка", "Некорректная сумма");
            return false;
        }

        return true;
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
