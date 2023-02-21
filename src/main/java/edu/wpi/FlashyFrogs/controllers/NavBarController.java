package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Theme;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

@GeneratedExclusion
public class NavBarController {

  @FXML private AnchorPane anchorPane;
  @FXML private HBox header;
  @FXML private Line line1;
  @FXML private Line line2;
  @FXML private Button homeButton;
  @FXML private Button helpButton;
  @FXML private Button srButton;
  @FXML private MenuButton menu;

  @FXML private MenuButton loggedOutMenu;

  @FXML
  public void initialize() {
    loggedOutMenu.setText("Welcome, Guest");
    loggedOutMenu.show();
    loggedOutMenu.setVisible(true);
    loggedOutMenu.setDisable(false);
    menu.setVisible(false);
    menu.setDisable(true);
    menu.hide();
    header.setDisable(true);
    header.setOpacity(0);
    //    srButton.setOpacity(0);
    //    srButton.setDisable(true);
    //    homeButton.setOpacity(0);
    //    homeButton.setDisable(true);
    //    helpButton.setOpacity(0);
    //    helpButton.setDisable(true);
    //    line1.setOpacity(0);
    //    line2.setOpacity(0);
  }

  public void logIn() {
    loggedOutMenu.setDisable(true);
    loggedOutMenu.setVisible(false);
    menu.setVisible(true);
    loggedOutMenu.hide();
    loggedOutMenu.setText("");
    menu.setDisable(false);
    header.setDisable(false);
    menu.setText("Welcome, " + CurrentUserEntity.CURRENT_USER.getCurrentUser().getFirstName());
    menu.show();
    header.setOpacity(1);
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
    if (Fapp.iController != null) {
      Fapp.iController.help();
    }
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
    menu.setText("");
    menu.setDisable(true);
    loggedOutMenu.setVisible(true);
    menu.setVisible(false);
    menu.hide();
    loggedOutMenu.setDisable(false);
    loggedOutMenu.setText("Welcome, Guest");
    loggedOutMenu.show();
    header.setDisable(true);
    header.setOpacity(0);
  }

  @FXML
  private void about() throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/About.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    popOver.detach();
    popOver.show(anchorPane.getScene().getWindow());
  }

  /**
   * Change the color theme between Dark and Light Mode when the Switch Color Scheme button is
   * clicked on NavBar.fxml.
   *
   * @throws IOException
   */
  public void changeMode() throws IOException {
    if (Fapp.getTheme().equals(Theme.LIGHT_THEME)) {
      Fapp.setTheme(Theme.DARK_THEME);
    } else {
      Fapp.setTheme(Theme.LIGHT_THEME);
    }
  }
}
