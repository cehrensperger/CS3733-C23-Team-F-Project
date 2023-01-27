package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.navigation.Navigation;
import edu.wpi.FlashyFrogs.navigation.Screen;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;

public class HomeController {

  @FXML MFXButton navigateButton;

  @FXML
  public void initialize() {
    navigateButton.setOnMouseClicked(event -> Navigation.navigate(Screen.SERVICE_REQUEST));
  }
}
