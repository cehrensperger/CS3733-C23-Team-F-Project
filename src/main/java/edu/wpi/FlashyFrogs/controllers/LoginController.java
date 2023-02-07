package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hibernate.Session;

public class LoginController {

  @FXML private AnchorPane rootPane;
  @FXML private MFXButton login;
  @FXML private MFXButton clear;
  @FXML private MenuItem newUserMenuItem;
  @FXML private MFXTextField username;
  @FXML private MFXPasswordField password;

  public void loginButton(ActionEvent actionEvent) throws Exception {
    // TODO if already in database
    Session ses = CONNECTION.getSessionFactory().openSession();
    try {
      // check if it's in the database
      // create new UserLogin(Username, Password)
      // if (UserLogin.get(Username) != -1 && UserLogin.checkPasswordEqual(Password))
      ses.close();
    } catch (Exception e) {
      ses.close();
      // TODO Show a popup or something
      throw e;
    }
    Fapp.setScene("Home");
    // TODO else, give "Incorrect Username or Password" popup or notification
    // going to need to check database somehow.
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
    Fapp.setScene("LoginAdministrator");
  }
}
