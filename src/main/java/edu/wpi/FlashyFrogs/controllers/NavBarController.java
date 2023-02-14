package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class NavBarController {

  @FXML private AnchorPane anchorPane;

  @FXML
  public void initialize() {}

  public AnchorPane getAnchorPane() {
    return anchorPane;
  }

  @FXML
  private void handleHomeButton(ActionEvent event) throws IOException {
    Fapp.setScene("views", "Home");
  }

  @FXML
  private void handleSignOut(ActionEvent event) throws IOException {
    Fapp.setScene("Account", "Login");
  }

  @FXML
  private void handleServiceRequestsButton(ActionEvent event) throws IOException {
    Fapp.setScene("views", "Credits");
  }

  @FXML
  private void handleHelpButton(ActionEvent event) throws IOException {
    Fapp.setScene("views", "Help");
  }

  @FXML
  private void closeApp() {
    Stage stage = (Stage) anchorPane.getScene().getWindow();
    stage.close();
  }
}
