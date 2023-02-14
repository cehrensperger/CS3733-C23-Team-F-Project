package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import java.io.IOException;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class NavBarController {

  @FXML private AnchorPane anchorPane;
  @FXML private Line line1;
  @FXML private Line line2;
  @FXML private MFXButton closeButton;
  @FXML private Button homeButton;
  @FXML private Button helpButton;
  @FXML private Button srButton;
  @FXML private MenuButton menu;

  @FXML
  public void initialize() {
    menu.hide();
    srButton.setOpacity(0);
    srButton.disarm();
    homeButton.setOpacity(0);
    homeButton.disarm();
    helpButton.setOpacity(0);
    helpButton.disarm();
    line1.setOpacity(0);
    line2.setOpacity(0);
  }

  public void logIn() {
    menu.show();
    srButton.setOpacity(1);
    srButton.arm();
    homeButton.setOpacity(1);
    homeButton.arm();
    helpButton.setOpacity(1);
    helpButton.arm();
    line1.setOpacity(1);
    line2.setOpacity(1);

    closeButton.setOpacity(0);
    closeButton.disarm();
    closeButton.setMouseTransparent(true);
  }

  public AnchorPane getAnchorPane() {
    return anchorPane;
  }

  @FXML
  private void handleHomeButton(ActionEvent event) throws IOException {
    Fapp.setScene("views", "Home");
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
