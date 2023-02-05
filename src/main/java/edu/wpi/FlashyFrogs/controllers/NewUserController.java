package edu.wpi.FlashyFrogs.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class NewUserController {

  @FXML private MFXButton newUser;
  @FXML private MFXTextField username;
  @FXML private MFXPasswordField pass1;
  @FXML private MFXPasswordField pass2;

  public void initialize() {}

  public void handleNewUser(ActionEvent actionEvent) throws IOException {}
}
