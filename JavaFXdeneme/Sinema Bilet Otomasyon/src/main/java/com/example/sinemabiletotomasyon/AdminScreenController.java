package com.example.sinemabiletotomasyon;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EventObject;

public class AdminScreenController {

    @FXML private TextField cityNameField;
    @FXML private TextField cityIdField;
    @FXML private TextField hallNameField;
    @FXML private TextField movieNameField;
    @FXML private TextField imageSourceField;
    @FXML private TextField seansTimeField;
    @FXML private Button saveBtn;
    @FXML private Button logoutBtn;

    @FXML
    private void initialize() {
        saveBtn.setOnAction(event -> handleSave());
    }
    
    @FXML
    private void handleSave() {
        try {
            int cityId = Integer.parseInt(cityIdField.getText());
            String cityName = cityNameField.getText();
            String hallName = hallNameField.getText();
            String movieName = movieNameField.getText();
            String imageSource = imageSourceField.getText();
            String seansTime = seansTimeField.getText();

            // 1. Şehri ekle
            DatabaseHelper.insertCity(cityId, cityName);

            // 2. Sinema salonunu ekle
            int cinemaId = DatabaseHelper.insertCinema(hallName, cityId);

            // 3. Filmi ekle
            int movieId = DatabaseHelper.insertMovie(movieName, imageSource);

            // 4. Seansı ekle
            DatabaseHelper.insertSeans(cinemaId, movieId, seansTime);

            // Başarılı alert göster
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Başarılı");
            alert.setHeaderText(null);
            alert.setContentText("Tüm işlemler başarıyla tamamlandı!");
            alert.showAndWait();


            cityIdField.clear();
            cityNameField.clear();
            hallNameField.clear();
            movieNameField.clear();
            imageSourceField.clear();
            seansTimeField.clear();


        } catch (Exception e) {
            e.printStackTrace();

            // Hata alert göster
            javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            errorAlert.setTitle("Hata");
            errorAlert.setHeaderText("İşlem sırasında bir hata oluştu");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
