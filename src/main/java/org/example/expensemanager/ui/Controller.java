package org.example.expensemanager.ui;

import org.example.expensemanager.data.ExpenseDao;
import org.example.expensemanager.data.ExpenseDaoFactory;
import org.example.expensemanager.business.model.Expense;
import org.example.expensemanager.business.model.ExpenseStatus;
import org.example.expensemanager.business.BudgetForecast;
import org.example.expensemanager.business.ExpenseCategoryManager;
import org.example.expensemanager.service.ExpenseService;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType;

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
    private ExpenseDaoFactory daoFactory;
    private ExpenseCategoryManager categoryManager;
    private ExpenseService expenseService;

    @FXML
    public void initialize() {
        daoFactory = new ExpenseDaoFactory();
        categoryManager = new ExpenseCategoryManager("categories.json");
        expenseDao = daoFactory.createMemoryDao();
        expenseService = new ExpenseService(expenseDao, categoryManager);

        categoryChoiceBox.getItems().addAll(categoryManager.getAvailableCategories());

        dataSourceChoiceBox.getItems().addAll("Память", "PostgreSQL", "JSON");
        dataSourceChoiceBox.setValue("Память");

        sortChoiceBox.getItems().addAll("По дате (новые)", "По дате (старые)",
                "По сумме (возрастание)", "По сумме (убывание)");
        sortChoiceBox.setValue("По дате (новые)");

        setupTableColumns();
        setupTimeSpinner();
        setupEventHandlers();
        datePicker.setValue(LocalDate.now());

        updateTableView();
    }

    private void setupTimeSpinner() {
        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<>() {
            {
                setConverter(new StringConverter<>() {
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    @Override public String toString(LocalTime time) {
                        return time != null ? formatter.format(time) : "";
                    }
                    @Override public LocalTime fromString(String string) {
                        return LocalTime.parse(string, formatter);
                    }
                });
                setValue(LocalTime.now().withSecond(0).withNano(0));
            }

            @Override public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps));
            }

            @Override public void increment(int steps) {
                setValue(getValue().plusMinutes(steps));
            }
        };
        timeSpinner.setValueFactory(valueFactory);
        timeSpinner.setEditable(true);
    }

    private void setupTableColumns() {
        TableColumn<Expense, Double> amountCol = new TableColumn<>("Сумма");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expense, String> descriptionCol = new TableColumn<>("Описание");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Expense, String> categoryCol = new TableColumn<>("Категория");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, String> dateCol = new TableColumn<>("Дата и время");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        dateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateTime().format(formatter)));

        TableColumn<Expense, String> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(cellData -> {
            ExpenseStatus status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.getDisplayName() : "Не указан");
        });

        expenseTable.getColumns().addAll(amountCol, descriptionCol, categoryCol, dateCol, statusCol);
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupEventHandlers() {
        selectDataSourceButton.setOnAction(event -> {
            switch (dataSourceChoiceBox.getValue()) {
                case "Память" -> expenseDao = daoFactory.createMemoryDao();
                case "PostgreSQL" -> expenseDao = daoFactory.createPostgresDao();
                case "JSON" -> expenseDao = daoFactory.createJsonDao();
            }
            expenseService = new ExpenseService(expenseDao, categoryManager);
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
            LocalDateTime selectedDateTime = LocalDateTime.of(datePicker.getValue(), timeSpinner.getValue());

            Expense expense = new Expense(amount, description, category, selectedDateTime);

            expenseService.add(expense);
            updateTableView();
            clearFields();
        } catch (IllegalArgumentException ex) {
            showAlert(AlertType.WARNING, "Дубликат", ex.getMessage());
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось добавить расход: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdateExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "Нет выбора", "Выберите расход для редактирования");
            return;
        }

        try {
            if (!validateFields()) return;

            selected.setAmount(Double.parseDouble(amountField.getText()));
            selected.setDescription(descriptionField.getText());
            selected.setCategory(categoryChoiceBox.getValue());
            selected.setDateTime(LocalDateTime.of(datePicker.getValue(), timeSpinner.getValue()));

            expenseService.update(selected);
            updateTableView();
            clearFields();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Ошибка", "Не удалось обновить расход: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteExpense() {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(AlertType.WARNING, "Нет выбора", "Выберите расход для удаления");
            return;
        }

        expenseService.delete(selected.getId());
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
        String filter = filterField.getText().toLowerCase();
        String sort = sortChoiceBox.getValue();
        List<Expense> all = expenseService.getAll();

        List<Expense> filtered = new ArrayList<>();
        for (Expense e : all) {
            if (filter.isEmpty() ||
                    e.getDescription().toLowerCase().contains(filter) ||
                    e.getCategory().toLowerCase().contains(filter) ||
                    String.valueOf(e.getAmount()).contains(filter)) {
                filtered.add(e);
            }
        }

        switch (sort) {
            case "По дате (новые)" -> filtered.sort((e1, e2) -> e2.getDateTime().compareTo(e1.getDateTime()));
            case "По дате (старые)" -> filtered.sort((e1, e2) -> e1.getDateTime().compareTo(e2.getDateTime()));
            case "По сумме (возрастание)" -> filtered.sort((e1, e2) -> Double.compare(e1.getAmount(), e2.getAmount()));
            case "По сумме (убывание)" -> filtered.sort((e1, e2) -> Double.compare(e2.getAmount(), e1.getAmount()));
        }

        expenseTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private void fillFormWithSelectedExpense(Expense e) {
        amountField.setText(String.valueOf(e.getAmount()));
        descriptionField.setText(e.getDescription());
        categoryChoiceBox.setValue(e.getCategory());
        datePicker.setValue(e.getDateTime().toLocalDate());
        timeSpinner.getValueFactory().setValue(e.getDateTime().toLocalTime());
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
            showAlert(AlertType.WARNING, "Проверка", "Заполните сумму и описание");
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

    private void showAlert(AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
