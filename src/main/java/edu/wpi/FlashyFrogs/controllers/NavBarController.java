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
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

@GeneratedExclusion
public class NavBarController {

  @FXML private MenuItem menuToggleSFX;
  @FXML private MenuItem loggedOutMenuToggleSFX;
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
    updateToggleSFX();
    //    srButton.setOpacity(0);
    //    srButton.setDisable(true);
    //    homeButton.setOpacity(0);
    //    homeButton.setDisable(true);
    //    helpButton.setOpacity(0);
    //    helpButton.setDisable(true);
    //    line1.setOpacity(0);
    //    line2.setOpacity(0);
  }

  /**
   * Updates the text on the menu item that toggles sound effects on and off to reflect whether
   * clicking the item will turn sound effects on or off: clicking the menu item will turn sound
   * effects on if sound effects are currently set to off, and vice versa
   */
  private void updateToggleSFX() {
    // if sounds are turned on, make menu option say that it turns sound off
    if (Fapp.isSfxOn()) {
      loggedOutMenuToggleSFX.setText("Turn Sound Effects Off");
      menuToggleSFX.setText("Turn Sound Effects Off");
      // if sounds are turned off, make menu option say that it turns sound on
    } else {
      loggedOutMenuToggleSFX.setText("Turn Sound Effects On");
      menuToggleSFX.setText("Turn Sound Effects On");
    }
  }

  public void logIn() {
    loggedOutMenu.setDisable(true);
    loggedOutMenu.setVisible(false);
    menu.setVisible(true);
    loggedOutMenu.hide();
    loggedOutMenu.setText("");
    updateToggleSFX();
    menu.setDisable(false);
    header.setDisable(false);
    menu.setText("Welcome, " + CurrentUserEntity.CURRENT_USER.getCurrentUser().getFirstName());
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
    Fapp.setScene("Accounts", "Login");
    signUserOutWithoutSceneChange();
  }

  /** */
  public void signUserOutWithoutSceneChange() {
    CurrentUserEntity.CURRENT_USER.setCurrentUser(null);
    menu.setText("");
    menu.setDisable(true);
    loggedOutMenu.setVisible(true);
    menu.setVisible(false);
    menu.hide();
    loggedOutMenu.setDisable(false);
    loggedOutMenu.setText("Welcome, Guest");
    header.setDisable(true);
    header.setOpacity(0);
  }

  /**
   * Launch About page when About This Application is clicked in the menu bar.
   *
   * @throws IOException
   */
  @FXML
  private void about() throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/About.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    popOver.detach();
    popOver.show(anchorPane.getScene().getWindow());
  }

  /**
   * Launch Acknowledgments page when Acknowledgments is clicked in the menu bar.
   *
   * @throws IOException
   */
  @FXML
  private void acknowledgments() throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/acknowledgmentsPage.fxml"));
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
  @FXML
  private void changeMode() throws IOException {
    if (Fapp.getTheme().equals(Theme.LIGHT_THEME)) {
      Fapp.setTheme(Theme.DARK_THEME);
    } else {
      Fapp.setTheme(Theme.LIGHT_THEME);
    }
  }

  /**
   * If sound effects were off, turn them on and say that clicking the menu option again will turn
   * them off. If sound effects were on, turn them off and say that clicking the menu option again
   * will turn them on.
   *
   * @param actionEvent
   */
  @FXML
  private void toggleSFX(ActionEvent actionEvent) {
    MenuItem menu = (MenuItem) actionEvent.getSource();
    if (Fapp.isSfxOn()) {
      Fapp.setSfxOn(false);
      menu.setText("Turn Sound Effects On");
    } else {
      Fapp.setSfxOn(true);
      menu.setText("Turn Sound Effects Off");
    }
  }
}
