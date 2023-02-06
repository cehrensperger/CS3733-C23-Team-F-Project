package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RequestsHomeController {

  @FXML private BorderPane rootPane;
  @FXML private Text homeText;
  @FXML private MFXButton internalPatientTransportationButton;
  @FXML private MFXButton sanitationServicesButton;
  @FXML private MFXButton securityServicesButton;
  @FXML private MFXButton exitButton;
  @FXML private MFXButton backButton;
  @FXML private MFXButton allRequestsButton;

  Stage stage;

  /** Initialize controller by FXML Loader. */
  @FXML
  public void initialize() {}

  @FXML
  public void handleSecurityServiceButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SecurityService");
  }

  @FXML
  public void handleSanitationServiceButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SanitationService");
  }

  @FXML
  public void handleInternalPatientTransportationButton(ActionEvent actionEvent)
      throws IOException {
    Fapp.setScene("Transport");
  }

  @FXML
  public void handleExitButton(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  @FXML
  public void handleBackButton(ActionEvent event) throws IOException {
    Fapp.setScene("Home");
  }

  @FXML
  public void handleAllRequestsButton(ActionEvent event) throws IOException {
    Fapp.setScene("AllRequests");
  }

  @FXML
  public void handleAudioVisualButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("AudioVisualService");
  }

  @FXML
  public void handleComputerServicesButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ComputerService");
  }
}
