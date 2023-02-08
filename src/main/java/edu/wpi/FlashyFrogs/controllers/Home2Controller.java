package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Home2Controller {

  @FXML private StackPane rootPane;
  @FXML private MFXButton serviceRequestsButton;
  @FXML private MFXButton mapDataEditorButton;
  @FXML private MFXButton pathfindingButton;
  @FXML private MFXButton exitButton;
  @FXML private MenuItem closeMenuItem;
  @FXML private MenuItem loadMapMenuItem;
  @FXML private MenuItem loadFeedbackMenuItem;
  @FXML private MFXButton secretButton;

  Stage stage;

  @FXML
  public void handleExitButton(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  @FXML
  public void handleClose(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  public void handleServiceRequestsButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome", "views");
  }

  public void handleMapDataEditorButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("DBTableEditor", "views");
  }

  public void handlePathfindingButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("PathFinding", "views");
  }

  public void handleSecurityMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SecurityService", "views");
  }

  public void handleTransportMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Transport", "views");
  }

  public void handleSanitationMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SanitationService", "views");
  }

  public void handleLoadMapMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("LoadMapPage", "views");
  }

  public void handleFeedbackMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Feedback", "views");
  }

  public void secretMethod(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home", "views");
  }
}
