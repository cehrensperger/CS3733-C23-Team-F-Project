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
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.*;
import javafx.stage.Stage;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

@GeneratedExclusion
public class LoginController implements IController {

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
                            for (UserLogin user : users.values()) {
                              if (user.checkRFIDBadgeEqual(backgroundText)) {
                                CurrentUserEntity.CURRENT_USER.setCurrentUser(user.getUser());
                                Fapp.setScene("views", "Home");
                                Fapp.logIn();
                                CurrentUserEntity.CURRENT_USER.setCurrentUser(user.getUser());
                                backgroundText = ""; // Clear the background text
                                return;
                              }
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
      errorMessage.setText("Please fill out all fields!");
      Sound.ERROR.play();
      errorMessage.setVisible(true);
    } else if (users.containsKey(username.getText())
        && users.get(username.getText()).checkPasswordEqual(password.getText())) {
      CurrentUserEntity.CURRENT_USER.setCurrentUser(users.get(username.getText()).getUser());
      Fapp.setScene("views", "Home");
      Fapp.logIn();
      CurrentUserEntity.CURRENT_USER.setCurrentUser(users.get(username.getText()).getUser());
      backgroundText = ""; // Clear the background text
    } else {
      // if we haven't exited by this point
      errorMessage.setText("Invalid Username or Password.");
      Sound.ERROR.play();
      errorMessage.setVisible(true);
    }
  }

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
    System.out.println("opening pathfinding");
    CurrentUserEntity.CURRENT_USER.setCurrentUser(
        new HospitalUser("a", "a", "a", HospitalUser.EmployeeType.STAFF, new Department()));
    Fapp.setScene("Pathfinding", "Pathfinding");
  }

  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }
}
