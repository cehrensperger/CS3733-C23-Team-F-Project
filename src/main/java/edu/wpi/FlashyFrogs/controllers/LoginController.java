package edu.wpi.FlashyFrogs.controllers;

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

public class LoginController {

  @FXML private AnchorPane rootPane;
  @FXML private MFXButton login;
  @FXML private MFXButton clear;
  @FXML private MenuItem newUserMenuItem;
  @FXML private MFXTextField username;
  @FXML private MFXPasswordField password;

  public void loginButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home");
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
