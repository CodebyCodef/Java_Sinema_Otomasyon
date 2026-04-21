module com.example.bundanolacak {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bundanolacak to javafx.fxml;
    exports com.example.bundanolacak;
}