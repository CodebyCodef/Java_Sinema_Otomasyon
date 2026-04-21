package com.example.sinemabiletotomasyon;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button dashBtn;

    @FXML
    private Button homeBtn;

    public void initialize() {
        try {
            showTicketScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showTicketScreen(String movie , String seans) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TicketScreen.fxml"));
        Pane ticketScreen = loader.load();

        // TicketScreenController'a referans gerekirse:
        TicketScreenController controller = loader.getController();
        controller.setMenuController(this);



        mainBorderPane.setCenter(ticketScreen);
    }

    public void showTicketScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TicketScreen.fxml"));
        Pane ticketScreen = loader.load();

        TicketScreenController controller = loader.getController();
        controller.setMenuController(this);

        mainBorderPane.setCenter(ticketScreen);
    }


    public void showBuyTicketScreen(String selectedMovie, String selectedSeans, String selectedCity , int seansID) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BuyTicketScreen.fxml"));
        Pane buyTicketScreen = loader.load();

        BuyTicketScreenController controller = loader.getController();
        controller.setMenuController(this);
        controller.setSelectedCity(selectedCity);
        controller.setSelectedMovie(selectedMovie);
        controller.setSelectedSeans(selectedSeans);
        controller.setSeansID(seansID);

        mainBorderPane.setCenter(buyTicketScreen);
    }


    @FXML
    private void logout(ActionEvent event) throws IOException {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader logoutThis = new FXMLLoader(getClass().getResource("LoginScreen.fxml"));
        Parent root = logoutThis.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void dashboard(ActionEvent event) throws IOException {
        Parent dashboardView = FXMLLoader.load(getClass().getResource("DashboardScreen.fxml"));
        mainBorderPane.setCenter(dashboardView);


    }

    @FXML
    private void home(ActionEvent event) throws IOException {
        showTicketScreen();
    }

}
