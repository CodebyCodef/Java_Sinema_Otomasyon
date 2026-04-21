package com.example.sinemabiletotomasyon;

import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlserver://CODEF;databaseName=sinauto;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "codef";
    private static final String PASS = "codef123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static int insertCity(int city_id, String city_name) throws SQLException {
        if (cityExists(city_id)) {
            return city_id; // varsa zaten ID'yi döndür
        }

        String query = "INSERT INTO city (city_id, city_name) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, city_id);
            stmt.setString(2, city_name);
            stmt.executeUpdate();
            return city_id;
        }
    }

    public static boolean cityExists(int city_id) throws SQLException {
        String query = "SELECT 1 FROM city WHERE city_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, city_id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public static int insertCinema(String cinema_name, int cityId) throws SQLException {
        String query = "INSERT INTO cinema (cinema_name, city_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cinema_name);
            stmt.setInt(2, cityId);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Cinema insert failed");
    }

    public static int insertMovie(String movie_title, String image_source) throws SQLException {
        String query = "INSERT INTO movie (movie_title, image_source) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, movie_title);
            stmt.setString(2, image_source);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Movie insert failed");
    }

    public static void insertSeans(int cinema_id , int movie_id , String times) throws SQLException {
        String query = "INSERT INTO seans (cinema_id, movie_id, times) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cinema_id);
            stmt.setInt(2, movie_id);
            stmt.setString(3, times);
            stmt.executeUpdate();
        }
    }
}
