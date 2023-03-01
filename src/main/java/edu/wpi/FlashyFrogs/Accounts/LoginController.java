package edu.wpi.FlashyFrogs.Accounts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Department;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.ORM.UserLogin;
// import edu.wpi.FlashyFrogs.controllers.ForgotPassController;
import edu.wpi.FlashyFrogs.Sound;
import edu.wpi.FlashyFrogs.controllers.ForgotPassController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.HashMap;
import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

@GeneratedExclusion
public class LoginController implements IController {

  public Pane errtoast;
  public Rectangle errcheck2;
  public Rectangle errcheck1;
  public Pane errtoast1;
  public Rectangle errcheck21;
  public Rectangle errcheck11;
  public BorderPane borderPane;
  @FXML private AnchorPane rootPane;
  @FXML private TextField username;
  @FXML private PasswordField password;
  @FXML private MFXButton login;
  @FXML private MFXButton clear;
  @FXML Text forgot;
  @FXML private Label errorMessage;

  /** Background text, used for RFID badge capture */
  private String backgroundText = "";

  @NonNull
  private final HashMap<String, UserLogin> users; // Users in the hospital, username to user

  /** Pre-loads all the users, username to user object */
  public LoginController() {
    users = new HashMap<>(); // Create the users map

    // With a session, populate the map
    try (Session session = CONNECTION.getSessionFactory().openSession()) {
      // Get the users, cache them
      session
          .createQuery("FROM UserLogin", UserLogin.class)
          .getResultList()
          .forEach((login) -> users.put(login.getUserName(), login));
    }
  }

  public void initialize() {

    // NavBarController newNav = new NavBarController();
    // newNav.guestPathfinding();
    Fapp.resetStackLogin();

    // Set up the key press handler
    Platform.runLater(
        () ->
            rootPane
                .getScene()
                .setOnKeyPressed(
                    (event -> {
                      if (event.getCode().equals(KeyCode.ENTER)) {
                        // If the username exists
                        if (!username.getText().isEmpty()) {
                          loginButton(null); // Try logging in
                        } else {
                          if (!backgroundText.equals("")) {
                            UserLogin user;
                            String id = backgroundText.substring(5, 10);
                            String pw = backgroundText.substring(0, 5);
                            System.out.println(id);
                            System.out.println(pw);
                            try (Session session = CONNECTION.getSessionFactory().openSession()) {
                              // Get the users, cache them
                              user =
                                  session
                                      .createQuery(
                                          "FROM UserLogin WHERE RFIDBadge = :id", UserLogin.class)
                                      .setParameter("id", id)
                                      .uniqueResult();
                            }
                            System.out.println(user.getUserName());
                            System.out.println(user.checkRFIDBadgeEqual(pw));
                            if (user.checkRFIDBadgeEqual(pw)) {
                              CurrentUserEntity.CURRENT_USER.setCurrentUser(user.getUser());
                              Fapp.setScene("views", "Home");
                              Fapp.logIn();
                              CurrentUserEntity.CURRENT_USER.setCurrentUser(user.getUser());
                              backgroundText = ""; // Clear the background text
                              return;
                            }

                            backgroundText = ""; // Clear the text otherwise
                          }
                        }
                      } else {
                        backgroundText += event.getText(); // Add the text to the RFID string
                      }
                    })));
  }

  @SneakyThrows
  public void loginButton(ActionEvent actionEvent) {
    if (username.getText().equals("") || password.getText().equals("")) {
      // One of the values is left null
      errortoastAnimation();
      fadeOutAnimation();
      login.setDisable(true);
      Sound.ERROR.play();

    } else if (users.containsKey(username.getText())
        && users.get(username.getText()).checkPasswordEqual(password.getText())) {
      CurrentUserEntity.CURRENT_USER.setCurrentUser(users.get(username.getText()).getUser());
      CurrentUserEntity.CURRENT_USER.setCurrentUser(users.get(username.getText()).getUser());
      Fapp.logIn();
      backgroundText = ""; // Clear the background text
    } else {
      // if we haven't exited by this point
      errortoastAnimation1();
      login.setDisable(true);
      Sound.ERROR.play();
    }
  }

  //  public void loginthread() {
  //    System.out.println("inLoginThread");
  //
  //  }

  public void forgotPass(MouseEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/ForgotPass.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    ForgotPassController forgotPass = newLoad.getController();
    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }

  public void handleClose(ActionEvent actionEvent) throws IOException {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    username.clear();
    password.clear();
  }

  public void handleNewUser(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "LoginAdministrator");
  }

  @FXML
  public void openPathfinding(ActionEvent event) throws IOException {
    //    System.out.println("opening pathfinding");
    CurrentUserEntity.CURRENT_USER.setCurrentUser(
        new HospitalUser("a", "a", "a", HospitalUser.EmployeeType.STAFF, new Department()));
    Fapp.setScene("Pathfinding", "Pathfinding");
  }

  public void errortoastAnimation() {
    errtoast.getTransforms().clear();
    errtoast.setLayoutX(0);

    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translate1.setByX(-280);
    translate1.setAutoReverse(true);
    errcheck1.setFill(Color.web("#012D5A"));
    errcheck2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck1, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck2, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.jumpTo(Duration.ZERO);
    sequence.playFromStart();
    sequence.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            login.setDisable(false);
          }
        });
  }

  public void fadeOutAnimation() {

    login.setDisable(true);
    FadeTransition fadout = new FadeTransition(Duration.seconds(1), rootPane);
    fadout.setFromValue(1);
    fadout.setToValue(0);
    fadout.setCycleCount(1);
    fadout.setAutoReverse(false);
    fadout.play();
    fadout.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            login.setDisable(false);
          }
        });
  }

  public void errortoastAnimation1() {
    errtoast1.getTransforms().clear();
    errtoast1.setLayoutX(0);

    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast1);
    translate1.setByX(-370);
    translate1.setAutoReverse(true);
    errcheck11.setFill(Color.web("#012D5A"));
    errcheck21.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck11, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck21, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast1);
    translateBack1.setDelay(Duration.seconds(0.6));
    translateBack1.setByX(370);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.jumpTo(Duration.ZERO);
    sequence.playFromStart();
    sequence.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            login.setDisable(false);
          }
        });
  }

  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }
}
