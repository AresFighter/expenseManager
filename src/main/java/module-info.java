module org.example.expensemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires java.dotenv;

    opens org.example.expensemanager to javafx.fxml, com.google.gson;
    exports org.example.expensemanager;
}