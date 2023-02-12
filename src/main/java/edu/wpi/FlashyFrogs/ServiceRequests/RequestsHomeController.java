package edu.wpi.FlashyFrogs.ServiceRequests;

import edu.wpi.FlashyFrogs.Fapp;
import java.io.IOException;

import edu.wpi.FlashyFrogs.controllers.HelpController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

public class RequestsHomeController {

  @FXML private BorderPane rootPane;

  Stage stage;

  /** Initialize controller by FXML Loader. */
  @FXML
  public void initialize() {}

  @FXML
  public void handleSecurityServiceButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "SecurityService");
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
    Fapp.setScene("views", "SanitationService");
  }

  @FXML
  public void handleInternalPatientTransportationButton(ActionEvent actionEvent)
      throws IOException {
    Fapp.setScene("views", "Transport");
  }

  @FXML
  public void handleExitButton(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  @FXML
  public void handleBackButton(ActionEvent event) throws IOException {
    Fapp.setScene("views", "Home");
  }

  @FXML
  public void handleAllRequestsButton(ActionEvent event) throws IOException {
    Fapp.setScene("views", "AllRequests");
  }

  @FXML
  public void handleAudioVisualButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "AudioVisualService");
  }

  @FXML
  public void handleComputerServicesButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "ComputerService");
  }
}
