module org.example.expensemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires java.dotenv;

    opens org.example.expensemanager.ui to javafx.fxml, com.google.gson;
    opens org.example.expensemanager.data to com.google.gson, javafx.fxml;
    opens org.example.expensemanager.service to com.google.gson, javafx.fxml;
    opens org.example.expensemanager.business to com.google.gson, javafx.fxml;
    opens org.example.expensemanager.data.daoimpl to com.google.gson, javafx.fxml;
    opens org.example.expensemanager.business.model to com.google.gson, javafx.fxml;

    exports org.example.expensemanager.ui;
    exports org.example.expensemanager.data;
    exports org.example.expensemanager.service;
    exports org.example.expensemanager.business;
    exports org.example.expensemanager.data.daoimpl;
    exports org.example.expensemanager.business.model;
}