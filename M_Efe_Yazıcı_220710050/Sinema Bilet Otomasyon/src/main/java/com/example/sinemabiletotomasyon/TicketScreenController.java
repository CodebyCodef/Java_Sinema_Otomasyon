package com.example.sinemabiletotomasyon;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class TicketScreenController {

    @FXML
    public int seansID;

    @FXML
    private ImageView movieBanner;

    @FXML
    private ComboBox<String> city_combobox;

    @FXML
    private ComboBox<String> hall_combobox;
    private Map<String, Integer> hallNameToIdMap = new HashMap<>();


    @FXML
    private ComboBox<String> movie_combobox;

    @FXML
    private ComboBox<String> seans_combobox;

    @FXML
    private Button ticketBuyBtn;

    private Connection connection;


    private MenuController menuController;

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }


    @FXML
    private void initialize() {
        connectToDatabase();
        loadCities();
        setupListeners();
    }

    @FXML
    private void setupListeners() {
        city_combobox.setOnAction(event -> {
            String selectedCity = city_combobox.getValue();
            if (selectedCity != null) {
                loadHallsByCity(selectedCity);
            }
        });

        hall_combobox.setOnAction(event -> {
            String selectedHall = hall_combobox.getValue();
            if (selectedHall != null) {
                loadMoviesByHall(selectedHall);
            }
        });

        movie_combobox.setOnAction(event -> {
            String selectedMovie = movie_combobox.getValue();
            if (selectedMovie != null) {
                loadSeansByMovie(selectedMovie);
                updateMovieBanner(selectedMovie);
            }
        });
    }

    @FXML
    private void loadCities() {
        try {
            String sql = "SELECT city_name FROM city";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            city_combobox.getItems().clear();
            while (rs.next()) {
                city_combobox.getItems().add(rs.getString("city_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadHallsByCity(String cityName) {
        try {
            String sql = "SELECT cinema_id,cinema_name FROM cinema WHERE city_id = (SELECT city_id FROM city WHERE city_name = ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cityName);
            ResultSet rs = stmt.executeQuery();

            hall_combobox.getItems().clear();
            while (rs.next()) {

                int cinemaId = rs.getInt("cinema_id");
                String cinemaName = rs.getString("cinema_name");

                hall_combobox.getItems().add(rs.getString("cinema_name"));
                hallNameToIdMap.put(cinemaName, cinemaId); // eşleştir

            }

            movie_combobox.getItems().clear();
            seans_combobox.getItems().clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadMoviesByHall(String hallName) {
        try {
            String selectedCity = city_combobox.getValue();
            if (selectedCity == null) return;

            String sql = """
        SELECT DISTINCT m.movie_title 
        FROM movie m
        JOIN seans s ON m.movie_id = s.movie_id
        JOIN cinema c ON s.cinema_id = c.cinema_id
        JOIN city ct ON c.city_id = ct.city_id
        WHERE c.cinema_name = ? AND ct.city_name = ?
        """;

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, hallName);
            stmt.setString(2, selectedCity);
            ResultSet rs = stmt.executeQuery();

            movie_combobox.getItems().clear();
            while (rs.next()) {
                movie_combobox.getItems().add(rs.getString("movie_title"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadSeansByMovie(String movieTitle) {
        try {
            String selectedHall = hall_combobox.getValue();
            if (selectedHall == null) return;

            Integer cinemaId = hallNameToIdMap.get(selectedHall);
            if (cinemaId == null) return; // güvenlik kontrolü


            String sql = """
        SELECT s.times
        FROM seans s
        JOIN movie m ON s.movie_id = m.movie_id
        WHERE m.movie_title = ? AND s.cinema_id = ?
        """;

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, movieTitle);
            stmt.setInt(2, cinemaId);
            ResultSet rs = stmt.executeQuery();

            seans_combobox.getItems().clear();
            while (rs.next()) {
                seans_combobox.getItems().add(rs.getString("times"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void updateMovieBanner(String movieTitle ) {
        try {

            String sql = "SELECT image_source FROM movie WHERE movie_title = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, movieTitle);
            ResultSet rs = stmt.executeQuery();


            if (rs.next()) {

                String sourcePath = rs.getString("image_source");
                System.out.println("Kaynak yolu: " + sourcePath);

                Image image = new Image(getClass().getResourceAsStream("films/" + sourcePath));
                movieBanner.setImage(image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void findSeansId ()
    {
        String cityName = city_combobox.getValue();
        if (cityName == null) return;

        String movieTitle = movie_combobox.getValue();
        if (movieTitle == null) return;

        String movieTime = seans_combobox.getValue();
        if (movieTime == null) return;

        try {


            String sql = """
            SELECT s.seans_id, m.image_source FROM seans s
            JOIN cinema c ON s.cinema_id = c.cinema_id
            JOIN city ct ON c.city_id = ct.city_id
            JOIN movie m ON s.movie_id = m.movie_id
            WHERE ct.city_name = ? AND m.movie_title = ? AND s.times = ?
            """;

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, cityName);      // cityName doğru parametre 1
            stmt.setString(2, movieTitle);    // movieTitle parametre 2
            stmt.setString(3, movieTime);     // movieTime parametre 3

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                seansID = rs.getInt("seans_id");


            }
            else {
                System.out.println("Seans bulunamadı!");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @FXML
    private void ticketBuy(ActionEvent event) {
        try {
            String selectedMovie = movie_combobox.getSelectionModel().getSelectedItem();
            String selectedSeans = seans_combobox.getSelectionModel().getSelectedItem();
            String selectedCity = city_combobox.getSelectionModel().getSelectedItem(); // şehri de seçiyorsan

            menuController.showBuyTicketScreen(selectedMovie, selectedSeans, selectedCity,seansID);
        } catch (IOException e) {
            e.printStackTrace();
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
