package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Theme;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

@GeneratedExclusion
public class NavBarController {

  @FXML private AnchorPane anchorPane;
  // @FXML private HBox header;
  @FXML private Line line1;
  @FXML private Line line2;
  @FXML private Button homeButton;
  @FXML private Button helpButton;
  @FXML private Button srButton;
  @FXML private MenuButton menu;

  @FXML private MenuButton loggedOutMenu;
  @FXML private Label clockLabel;

  @FXML
  public void initialize() {
    loggedOutMenu.setText("Welcome, Guest");
    loggedOutMenu.show();
    loggedOutMenu.setVisible(true);
    loggedOutMenu.setDisable(false);
    menu.setVisible(false);
    menu.setDisable(true);
    menu.hide();
    // header.setDisable(true);
    // header.setOpacity(0);
    //    srButton.setOpacity(0);
    //    srButton.setDisable(true);
    //    homeButton.setOpacity(0);
    //    homeButton.setDisable(true);
    //    helpButton.setOpacity(0);
    //    helpButton.setDisable(true);
    //    line1.setOpacity(0);
    //    line2.setOpacity(0);

    dateAndTime();
    clockLabel.setTextFill(Paint.valueOf("white"));
  }

  public void logIn() {
    loggedOutMenu.setDisable(true);
    loggedOutMenu.setVisible(false);
    menu.setVisible(true);
    loggedOutMenu.hide();
    loggedOutMenu.setText("");
    menu.setDisable(false);
    // header.setDisable(false);
    menu.setText("Welcome, " + CurrentUserEntity.CURRENT_USER.getCurrentUser().getFirstName());
    // header.setOpacity(1);
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
    // header.setDisable(true);
    // header.setOpacity(0);
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
  public void changeMode() throws IOException {
    if (Fapp.getTheme().equals(Theme.LIGHT_THEME)) {
      Fapp.setTheme(Theme.DARK_THEME);
    } else {
      Fapp.setTheme(Theme.LIGHT_THEME);
    }
  }

  public void dateAndTime() {
    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0),
                event -> {
                  SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm a");
                  Date date = new Date();
                  String formattedDate = sdf.format(date);
                  clockLabel.setText(formattedDate);
                }),
            new KeyFrame(Duration.seconds(1)));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
  }
}
