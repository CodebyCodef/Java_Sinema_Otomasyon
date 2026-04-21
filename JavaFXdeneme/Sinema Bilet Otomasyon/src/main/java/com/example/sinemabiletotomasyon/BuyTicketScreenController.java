package com.example.sinemabiletotomasyon;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BuyTicketScreenController {

    @FXML
    private TextField movieTextField;

    @FXML
    private TextField seansTextField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;


    @FXML
    private ImageView seatIMG;

    @FXML
    private ComboBox<String> seatCombo;

    @FXML
    private Button backBtn;

    @FXML
    private Button buyBtn;

    private int seansID;

    private String movie;
    private String seans;
    private String city;

    private Connection connection;


    private MenuController menuController;



    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }

    @FXML
    private void initialize (){

        setIMG();
        addSeat();
        connectToDatabase();

    }

    public void setSeansID(int seansID) {
        this.seansID = seansID;
    }


    public void setSelectedCity(String city) {
        this.city = city;

    }

    // Bu metotlarla dışarıdan değer atanacak
    public void setSelectedMovie(String movie) {
        this.movie = movie;
        movieTextField.setText(movie);
        movieTextField.setEditable(false); // değiştirilmesin
    }

    public void setSelectedSeans(String seans) {
        this.seans = seans;
        seansTextField.setText(seans);
        seansTextField.setEditable(false);
    }

    @FXML
    private void addSeat() {
        for (int i = 1; i <= 15; i++) {
            seatCombo.getItems().add("Koltuk " + i);  // Örnek: "Koltuk 1", "Koltuk 2" ...
        }
    }

    @FXML
    private void setIMG () {
        Image image = new Image(getClass().getResourceAsStream("img/seatIMG.png"));
        seatIMG.setImage(image);
    }

    @FXML
    private void backBtnClicked(ActionEvent event) {
        try {
            menuController.showTicketScreen(movie,seans);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void buyBtnClicked(ActionEvent event) {
        String first_name = nameField.getText();
        String last_name = surnameField.getText();
        String movie = movieTextField.getText();
        String seans = seansTextField.getText();
        String seatText = seatCombo.getSelectionModel().getSelectedItem();

        if (seatText == null || seatText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Koltuk Seçimi");
            alert.setHeaderText("Koltuk Seçilmedi");
            alert.setContentText("Lütfen bir koltuk seçiniz.");
            alert.showAndWait();
            return;
        }

        int seatNumber;
        try {
            seatNumber = Integer.parseInt(seatText.replace("Koltuk ", "").trim());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hatalı Koltuk Numarası");
            alert.setHeaderText("Koltuk numarası geçersiz");
            alert.setContentText("Lütfen geçerli bir koltuk numarası seçiniz.");
            alert.showAndWait();
            return;
        }

        int seans_id = -1;

        // Seans ID sorgulaması
        String seansQuery = "SELECT s.seans_id " +
                "FROM seans s " +
                "JOIN movie m ON s.movie_id = m.movie_id " +
                "JOIN cinema c ON s.cinema_id = c.cinema_id " +
                "JOIN city ct ON ct.city_id = c.city_id " +
                "WHERE m.movie_title = ? AND s.times = ? AND ct.city_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(seansQuery)) {
            pstmt.setString(1, movie);
            pstmt.setString(2, seans);
            pstmt.setString(3, city);

            var rs = pstmt.executeQuery();
            if (rs.next()) {
                seans_id = rs.getInt("seans_id");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Seans Bulunamadı");
                alert.setHeaderText(null);
                alert.setContentText("Seçilen bilgilere uygun seans bulunamadı.");
                alert.showAndWait();
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Veritabanı Hatası");
            alert.setHeaderText(null);
            alert.setContentText("Seans ID sorgusunda hata oluştu.");
            alert.showAndWait();
            return;
        }

        // Yeni eklenen: Aynı seans ve koltukta bilet var mı kontrolü
        String checkSeatQuery = "SELECT COUNT(*) AS count FROM ticket WHERE seans_id = ? AND seat_number = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSeatQuery)) {
            checkStmt.setInt(1, seans_id);
            checkStmt.setInt(2, seatNumber);

            var rs = checkStmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Koltuk Dolu");
                    alert.setHeaderText(null);
                    alert.setContentText("Seçtiğiniz koltuk zaten dolu. Lütfen başka bir koltuk seçiniz.");
                    alert.showAndWait();
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Veritabanı Hatası");
            alert.setHeaderText(null);
            alert.setContentText("Koltuk kontrolü sırasında hata oluştu.");
            alert.showAndWait();
            return;
        }

        // Bileti kaydetme işlemi
        String insertTicket = "INSERT INTO ticket (first_name, last_name, seat_number, seans_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertTicket)) {
            pstmt.setString(1, first_name);
            pstmt.setString(2, last_name);
            pstmt.setInt(3, seatNumber);
            pstmt.setInt(4, seans_id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilet Satın Alım");
                alert.setHeaderText("Başarılı");
                alert.setContentText("Biletiniz başarıyla kaydedildi.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setHeaderText(null);
                alert.setContentText("Bilet kaydı başarısız oldu.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Veritabanı Hatası");
            alert.setHeaderText(null);
            alert.setContentText("Bilet kaydı sırasında hata oluştu.");
            alert.showAndWait();
        }
    }




    @FXML
    private void connectToDatabase() {
        String dbUrl = "jdbc:sqlserver://CODEF;databaseName=sinauto;encrypt=true;trustServerCertificate=true;";
        String dbUser = "codef";
        String dbPass = "codef123";

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            System.out.println("Dashboard ekranında veritabanına bağlanıldı.");
        } catch (SQLException e) {
            System.out.println("Dashboard ekranında veritabanı bağlantı hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }



}





