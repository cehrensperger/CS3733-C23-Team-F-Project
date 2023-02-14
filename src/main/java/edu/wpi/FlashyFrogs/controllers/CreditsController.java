package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class CreditsController {

  @FXML MFXButton credits;
  @FXML MFXButton back;
  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "ITService");
  }

  public void handleIPT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "TransportService");
  }

  public void handleSanitation(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SanitationService");
  }

  public void handleSecurity(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SecurityService");
  }

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Credits");
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Home");
  }
}
