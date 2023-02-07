package edu.wpi.FlashyFrogs.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NewUserController {

  @FXML private MFXButton newUser;
  @FXML private MFXTextField username;
  @FXML private MFXPasswordField pass1;
  @FXML private MFXPasswordField pass2;
  @FXML private Label errorMessage;

  public void initialize() {}

  public void handleNewUser(ActionEvent actionEvent) throws IOException {
    if (username.getText().equals("") || pass1.getText().equals("") || pass2.getText().equals("")) {
      // One of the values is left null
      errorMessage.setText("Please fill out all fields!");
      errorMessage.setVisible(true);
    } else if (!pass1.getText().equals(pass2.getText())) {
      // Passwords do not match
      errorMessage.setText("Passwords do not match!");
      errorMessage.setVisible(true);
    } else {
      // Save Username and Password to db
      errorMessage.setVisible(false);
    }
  }
}
