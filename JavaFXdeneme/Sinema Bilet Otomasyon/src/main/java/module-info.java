module com.example.sinemabiletotomasyon {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.sinemabiletotomasyon to javafx.fxml;
    exports com.example.sinemabiletotomasyon;
}