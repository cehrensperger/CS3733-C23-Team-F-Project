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

  Stage stage;

  /** Initialize controller by FXML Loader. */
  @FXML
  public void initialize() {
    System.out.println("I am from HomeController.");

    //    submit.setOnMouseClicked(event -> {});
  }

  /**
   * FXML Injected Method that handles the submit button.
   *
   * @param event The event that triggered the method.
   */
  @FXML
  private void handleButtonSubmit(ActionEvent event) {
    /*
     1. Get the text the user input
     2. Validate it against the correct information
     3. Display to the user whether their input was correct or not
    */

  }

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
}
