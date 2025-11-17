module com.example.ums {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ums to javafx.fxml;
    exports com.example.ums;
}