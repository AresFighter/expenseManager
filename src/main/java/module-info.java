module org.example.expensemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires java.dotenv;

    opens org.example.expensemanager to javafx.fxml, com.google.gson;
    exports org.example.expensemanager;
    //exports core;
    //opens core to com.google.gson, javafx.fxml;
    exports core.model;
    opens core.model to com.google.gson, javafx.fxml;
    exports core.service;
    opens core.service to com.google.gson, javafx.fxml;
    exports core.dao;
    opens core.dao to com.google.gson, javafx.fxml;
    exports core.util;
    opens core.util to com.google.gson, javafx.fxml;
    exports org.example.expensemanager.ui;
    opens org.example.expensemanager.ui to com.google.gson, javafx.fxml;
    exports org.example.expensemanager.data;
    opens org.example.expensemanager.data to com.google.gson, javafx.fxml;
    exports org.example.expensemanager.service;
    opens org.example.expensemanager.service to com.google.gson, javafx.fxml;
    exports org.example.expensemanager.business;
    opens org.example.expensemanager.business to com.google.gson, javafx.fxml;
    exports org.example.expensemanager.data.daoimpl;
    opens org.example.expensemanager.data.daoimpl to com.google.gson, javafx.fxml;
    exports org.example.expensemanager.business.model;
    opens org.example.expensemanager.business.model to com.google.gson, javafx.fxml;
}