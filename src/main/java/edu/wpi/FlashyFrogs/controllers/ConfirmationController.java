package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ConfirmationController {

  @FXML private MFXButton home;

  @FXML
  public void homeButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home");
  }
}
