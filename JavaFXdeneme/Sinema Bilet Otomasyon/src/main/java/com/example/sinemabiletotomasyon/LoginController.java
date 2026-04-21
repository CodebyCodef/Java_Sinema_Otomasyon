package com.example.sinemabiletotomasyon;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.*;

public class LoginController {
    @FXML
    private Label welcomeText;

    @FXML
    private AnchorPane kullaniciPane;

    @FXML
    private AnchorPane adminPane;

    @FXML
    private TextField kullaniciID;

    @FXML
    private PasswordField kullaniciPassword;

    @FXML
    private TextField adminID;

    @FXML
    private PasswordField adminPassword;

    @FXML
    private Button adminGrsButton;

    @FXML
    private Button kullaniciGrsButton;

    @FXML
    private void kullaniciGirisYap() {

        kullaniciGrsButton.setStyle("-fx-border-radius:4;-fx-background-color: #cce5ff; -fx-border-color: #3399ff;");
        adminGrsButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        kullaniciPane.setVisible(true);
        adminPane.setVisible(false);
    }

    @FXML
    private void adminGirisYap() {
        kullaniciGrsButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        adminGrsButton.setStyle("-fx-border-radius:4;-fx-background-color: #cce5ff; -fx-border-color: #3399ff;");
        adminPane.setVisible(true);
        kullaniciPane.setVisible(false);
    }

    @FXML
    private void NgirisYap(ActionEvent event) {
        String kullanici_adi = kullaniciID.getText();
        String kullanici_sifre = kullaniciPassword.getText();

        String dbUrl = "jdbc:sqlserver://CODEF;databaseName=sinauto;encrypt=true;trustServerCertificate=true;";

        String dbKullanici = "codef";
        String dbSifre = "codef123";

        Connection baglanti = null;
        PreparedStatement sorgu = null;
        ResultSet sonuc = null;

        try {
            baglanti = DriverManager.getConnection(dbUrl, dbKullanici, dbSifre);

            String sql = "SELECT kullanici_adi FROM users\n" +
                    "WHERE kullanici_adi COLLATE Latin1_General_CS_AS = ?\n" +
                    "  AND kullanici_sifre COLLATE Latin1_General_CS_AS = ?\n" +
                    "AND stat = 'normal'" ;
            sorgu = baglanti.prepareStatement(sql);
            sorgu.setString(1, kullanici_adi);
            sorgu.setString(2, kullanici_sifre);

            sonuc = sorgu.executeQuery();

            if (sonuc.next()) {
                // Giriş başarılı
                showAlert(Alert.AlertType.INFORMATION, "Başarılı",
                        "Giriş Başarılı!", "Hoş geldiniz " + kullanici_adi);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuScreen.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } else {
                // Giriş başarısız
                showAlert(Alert.AlertType.ERROR, "Hata",
                        "Giriş Başarısız", "Kullanıcı adı veya şifre yanlış!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
                    "Bağlantı hatası oluştu", "Hata detayı: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (sonuc != null) sonuc.close();
                if (sorgu != null) sorgu.close();
                if (baglanti != null) baglanti.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void AgirisYap(ActionEvent event) {
        String kullanici_adi = adminID.getText();
        String kullanici_sifre = adminPassword.getText();

        String dbUrl = "jdbc:sqlserver://CODEF;databaseName=sinauto;encrypt=true;trustServerCertificate=true;";

        String dbKullanici = "codef";
        String dbSifre = "codef123";

        Connection baglanti = null;
        PreparedStatement sorgu = null;
        ResultSet sonuc = null;

        try {
            baglanti = DriverManager.getConnection(dbUrl, dbKullanici, dbSifre);

            String sql = "SELECT kullanici_adi FROM users\n" +
                    "WHERE kullanici_adi COLLATE Latin1_General_CS_AS = ?\n" +
                    "  AND kullanici_sifre COLLATE Latin1_General_CS_AS = ?\n" +
                    "AND stat = 'admin'" ;
            sorgu = baglanti.prepareStatement(sql);
            sorgu.setString(1, kullanici_adi);
            sorgu.setString(2, kullanici_sifre);

            sonuc = sorgu.executeQuery();

            if (sonuc.next()) {
                // Giriş başarılı
                showAlert(Alert.AlertType.INFORMATION, "Başarılı",
                        "Giriş Başarılı!", "Hoş geldiniz " + kullanici_adi);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminScreen.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } else {
                // Giriş başarısız
                showAlert(Alert.AlertType.ERROR, "Hata",
                        "Giriş Başarısız", "Kullanıcı adı veya şifre yanlış!");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
                    "Bağlantı hatası oluştu", "Hata detayı: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (sonuc != null) sonuc.close();
                if (sorgu != null) sorgu.close();
                if (baglanti != null) baglanti.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
