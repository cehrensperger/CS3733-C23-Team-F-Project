package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

public class RequestsHomeController {

  @FXML private BorderPane rootPane;
  @FXML private Text homeText;
  @FXML private MFXButton internalPatientTransportationButton;
  @FXML private MFXButton sanitationServicesButton;
  @FXML private MFXButton securityServicesButton;
  @FXML private MFXButton exitButton;
  @FXML private MFXButton backButton;
  @FXML private MFXButton allRequestsButton;
  @FXML private MFXButton question;

  Stage stage;

  /** Initialize controller by FXML Loader. */
  @FXML
  public void initialize() {}

  @FXML
  public void handleSecurityServiceButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SecurityService", "views");
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQRequestsHome();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }

  @FXML
  public void handleSanitationServiceButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SanitationService", "views");
  }

  @FXML
  public void handleInternalPatientTransportationButton(ActionEvent actionEvent)
      throws IOException {
    Fapp.setScene("Transport", "views");
  }

  @FXML
  public void handleExitButton(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  @FXML
  public void handleBackButton(ActionEvent event) throws IOException {
    Fapp.setScene("Home", "views");
  }

  @FXML
  public void handleAllRequestsButton(ActionEvent event) throws IOException {
    Fapp.setScene("AllRequests", "views");
  }

  @FXML
  public void handleAudioVisualButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("AudioVisualService", "views");
  }

  @FXML
  public void handleComputerServicesButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ComputerService", "views");
  }
}
