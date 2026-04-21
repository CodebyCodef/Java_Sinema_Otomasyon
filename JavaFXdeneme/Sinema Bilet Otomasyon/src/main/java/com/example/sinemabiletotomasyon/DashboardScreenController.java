package com.example.sinemabiletotomasyon;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DashboardScreenController implements Initializable {

    @FXML
    private GridPane movieGridPane;


    @FXML
    private Label sumCinemaLabel;

    @FXML
    private Label sumCityLabel;

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private ComboBox<String> cinemaComboBox;

    @FXML
    private ComboBox<String> movieComboBox;

    @FXML
    private Label emptySeatLabel;

    private Connection connection;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        connectToDatabase();
        loadCities();
        setupListeners();

        int sehirSayisi = getCitySum(); // veritabanından veya başka yerden çek
        int salonSayisi = getCinemaSum();

        sumCityLabel.setText(sehirSayisi + " Adet Şehir");
        sumCinemaLabel.setText(salonSayisi + " Adet Sinema Salonu");

        loadMoviesToGrid();
    }

    private int getCitySum() {

        int cityNum = 0;
        try {
            String sql = "SELECT  COUNT(DISTINCT city_name) as cityNum FROM city";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                cityNum = rs.getInt("cityNum");
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cityNum;
    }

    private int getCinemaSum() {

        int cinemaNum = 0;
        try {
            String sql = "SELECT COUNT(cinema_id) as cinemaNum FROM cinema";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                cinemaNum = rs.getInt("cinemaNum");
            }
            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cinemaNum;
    }


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

    private void setupListeners() {
        cityComboBox.setOnAction(e -> loadCinema());
        cinemaComboBox.setOnAction(e -> {
            if (cinemaComboBox.getValue() != null) {
                loadMovies(cinemaComboBox.getValue());
            }
        });
        movieComboBox.setOnAction(e -> updateEmptySeats());
    }

    private void loadCities() {
        try {
            String sql = "SELECT DISTINCT city_name FROM city";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cityComboBox.getItems().add(rs.getString("city_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void loadCinema() {
        cinemaComboBox.getItems().clear();
        String selectedCity = cityComboBox.getValue();
        try {
            String sql = "SELECT cinema_name FROM cinema WHERE city_id = (SELECT city_id FROM city WHERE city_name = ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, selectedCity);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cinemaComboBox.getItems().add(rs.getString("cinema_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMovies(String hallName) {
        movieComboBox.getItems().clear();
        String selectedCity = cityComboBox.getValue();

        try {
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

            while (rs.next()) {
                movieComboBox.getItems().add(rs.getString("movie_title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMoviesToGrid() {
        try {
            String sql = "SELECT movie_title FROM movie";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int column = 0;
            int row = 0;

            while (rs.next()) {
                String title = rs.getString("movie_title");

                Label movieLabel = new Label("-->"+title);
                movieLabel.getStyleClass().add("movie-grid-label");
                movieGridPane.setHgap(5);
                movieGridPane.setVgap(5);
                movieLabel.setAlignment(Pos.CENTER);
                GridPane.setHalignment(movieLabel, HPos.CENTER );


                movieGridPane.add(movieLabel, column, row);

                column++;
                if (column == 2) { // 2 sütunlu yapıda 2'den sonra bir alt satıra geç
                    column = 0;
                    row++;
                }
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateEmptySeats() {
        String city = cityComboBox.getValue();
        String hall = cinemaComboBox.getValue();
        String movie = movieComboBox.getValue();

        if (city == null || hall == null || movie == null) return;

        try {
            String seansSql = """
            SELECT s.seans_id
            FROM seans s
            JOIN cinema c ON s.cinema_id = c.cinema_id
            JOIN city ci ON c.city_id = ci.city_id
            JOIN movie m ON s.movie_id = m.movie_id
            WHERE ci.city_name = ?
            AND c.cinema_name = ?
            AND m.movie_title = ?
        """;

            PreparedStatement seansStmt = connection.prepareStatement(seansSql);
            seansStmt.setString(1, city);
            seansStmt.setString(2, hall);
            seansStmt.setString(3, movie);
            ResultSet seansRs = seansStmt.executeQuery();

            if (!seansRs.next()) {
                emptySeatLabel.setText("Seans bulunamadı");
                return;
            }

            int seansId = seansRs.getInt("seans_id");

            // 2. Bu seans için satılan bilet sayısını bul
            String ticketSql = "SELECT COUNT(*) AS occupied FROM ticket WHERE seans_id = ?";
            PreparedStatement ticketStmt = connection.prepareStatement(ticketSql);
            ticketStmt.setInt(1, seansId);
            ResultSet ticketRs = ticketStmt.executeQuery();

            if (ticketRs.next()) {
                int occupiedSeats = ticketRs.getInt("occupied");
                int emptySeats = 15 - occupiedSeats;
                emptySeatLabel.setText(String.valueOf(emptySeats));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            emptySeatLabel.setText("Hata");
        }
    }
}


