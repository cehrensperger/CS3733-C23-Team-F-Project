package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

@GeneratedExclusion
public class NavBarController {

  @FXML private AnchorPane anchorPane;
  @FXML private Line line1;
  @FXML private Line line2;
  @FXML private Button homeButton;
  @FXML private Button helpButton;
  @FXML private Button srButton;
  @FXML private MenuButton menu;
  @FXML private MFXButton closeButton;

  @FXML
  public void initialize() {
    menu.setDisable(true);
    menu.hide();
    srButton.setOpacity(0);
    srButton.setDisable(true);
    homeButton.setOpacity(0);
    homeButton.setDisable(true);
    helpButton.setOpacity(0);
    helpButton.setDisable(true);
    line1.setOpacity(0);
    line2.setOpacity(0);
  }

  public void logIn() {
    menu.show();
    menu.setDisable(false);
    menu.setText("Welcome, " + CurrentUserEntity.CURRENT_USER.getCurrentuser().getFirstName());
    srButton.setOpacity(1);
    srButton.setDisable(false);
    homeButton.setOpacity(1);
    homeButton.setDisable(false);
    helpButton.setOpacity(1);
    helpButton.setDisable(false);
    line1.setOpacity(1);
    line2.setOpacity(1);
    closeButton.setDisable(true);
    closeButton.setOpacity(0);
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
  private void handleSignOut(ActionEvent event) throws IOException {
    Fapp.setScene("Account", "Login");
  }

  @FXML
  private void handleServiceRequestsButton(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "Credits");
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

  @FXML
  private void signOut() {
    CurrentUserEntity.CURRENT_USER.setCurrentUser(null);
    Fapp.setScene("Accounts", "Login");
  }
}
